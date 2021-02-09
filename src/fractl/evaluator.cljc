(ns fractl.evaluator
  "Helper functions for compiling and evaluating patterns."
  (:require [fractl.component :as cn]
            [fractl.compiler :as c]
            [fractl.env :as env]
            [fractl.util :as u]
            [fractl.store :as store]
            [fractl.resolver.registry :as rr]
            [fractl.lang.internal :as li]
            [fractl.lang.opcode :as opc]
            [fractl.evaluator.internal :as i]
            [fractl.evaluator.root :as r]))

(defn- dispatch-an-opcode [evaluator env opcode]
  (if-let [f ((opc/op opcode) i/dispatch-table)]
    (f evaluator env (opc/arg opcode))
    (u/throw-ex (str "no dispatcher for opcode - " (opc/op opcode)))))

(defn dispatch [evaluator env {opcode :opcode}]
  (if (map? opcode)
    (dispatch-an-opcode evaluator env opcode)
    (loop [opcs opcode, env env, result nil]
      (if-let [opc (first opcs)]
        (let [r (dispatch-an-opcode evaluator env opc)]
          (recur (rest opcs) (or (:env r) env) r))
        result))))

(def ok? i/ok?)
(def dummy-result i/dummy-result)

(defn- dispatch-opcodes [evaluator env opcodes]
  (if (map? opcodes)
    (dispatch evaluator env opcodes)
    (loop [dc opcodes, result (dummy-result env)]
      (if (ok? result)
        (if-let [opcode (first dc)]
          (recur (rest dc) (dispatch evaluator (:env result) opcode))
          result)
        result))))

(defn eval-dataflow
  "Evaluate a compiled dataflow, triggered by event-instance, within the context
   of the provided environment. Each compiled pattern is dispatched to an evaluator,
   where the real evaluation is happening. Return the value produced by the resolver."
  ([evaluator env event-instance df]
   (let [env (if event-instance
               (env/bind-instance
                env (li/split-path (cn/instance-name event-instance))
                event-instance)
               env)
         [_ dc] (cn/dataflow-opcode df)]
     (dispatch-opcodes evaluator env dc)))
  ([evaluator event-instance df] (eval-dataflow evaluator env/EMPTY event-instance df)))

(defn run-dataflows
  "Compile and evaluate all dataflows attached to an event. The query-compiler
   and evaluator returned by a previous call to evaluator/make may be passed as
   the first two arguments."
  [compile-query-fn evaluator env event-instance]
  (let [dfs (c/compile-dataflows-for-event compile-query-fn event-instance)]
    (map #(eval-dataflow evaluator env event-instance %) dfs)))

(defn make
  "Use the given store to create a query compiler and pattern evaluator.
   Return the vector [compile-query-fn, evaluator]."
  [store]
  (let [cq (when store
             (partial store/compile-query store))]
    [cq (r/get-default-evaluator (partial run-dataflows cq) dispatch-opcodes)]))

(defn- store-from-config
  [store-or-store-config]
  (cond
    (or (nil? store-or-store-config)
        (map? store-or-store-config))
    (store/open-default-store store-or-store-config)

    (and (keyword? store-or-store-config)
         (= store-or-store-config :none))
    nil

    :else
    store-or-store-config))

(defn- resolver-from-config
  [resolver-or-resolver-config]
  (cond
    (nil? resolver-or-resolver-config)
    (rr/registered-resolvers)

    (map? resolver-or-resolver-config)
    (rr/register-resolvers resolver-or-resolver-config)

    (and (keyword? resolver-or-resolver-config)
         (= resolver-or-resolver-config :none))
    nil

    :else
    resolver-or-resolver-config))

(defn evaluator
  ([store-or-store-config resolver-or-resolver-config with-query-support]
   (let [store (store-from-config store-or-store-config)
         resolver (resolver-from-config resolver-or-resolver-config)
         [compile-query-fn evaluator] (make store)
         env (env/make store resolver)
         ef (partial run-dataflows compile-query-fn evaluator env)]
     (if with-query-support
       (fn [x]
         (if-let [qinfo (:Query x)]
           (r/find-instances env store (first qinfo) (second qinfo))
           (ef x)))
       ef)))
  ([store-or-store-config resolver-or-resolver-config]
   (evaluator store-or-store-config resolver-or-resolver-config false))
  ([] (evaluator nil nil)))

(defn- maybe-init-event [event-obj]
  (if (cn/event-instance? event-obj)
    event-obj
    (let [event-name (first (keys event-obj))]
      (cn/make-instance event-name (event-name event-obj)))))

(defn eval-all-dataflows
  ([event-obj store-or-store-config resolver-or-resolver-config]
   ((evaluator store-or-store-config resolver-or-resolver-config)
    (maybe-init-event event-obj)))
  ([event-obj]
   (eval-all-dataflows event-obj nil nil)))

(defn eval-pure-dataflows
  "Facility to evaluate dataflows without producing any side-effects.
   This is useful for pure tasks like data-format transformation.
   An example: transforming data before being sent to a resolver to
   match the format requirements of the backend"
  [event-obj]
  (eval-all-dataflows event-obj :none :none))

(defn- filter-public-result [xs]
  (if (map? xs)
    (dissoc xs :env)
    (doall (map filter-public-result xs))))

(defn public-evaluator [store-config with-query-support]
  (comp filter-public-result (evaluator store-config with-query-support)))
