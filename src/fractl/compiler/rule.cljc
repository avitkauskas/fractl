(ns fractl.compiler.rule
  "Parsing and compilations of the embedded rule language"
  (:require [clojure.string :as s]
            [fractl.util.logger :as log]
            [fractl.lang.internal :as li]
            #?(:cljs [cljs.js])))

(defn in [xs x]
  (some #{x} xs))

(defn lt [a b]
  (= -1 (compare a b)))

(defn lteq [a b]
  (let [c (compare a b)]
    (or (= c 0) (= c -1))))

(defn gt [a b]
  (= 1 (compare a b)))

(defn gteq [a b]
  (let [c (compare a b)]
    (or (= c 0) (= c 1))))

(defn neq [a b]
  (not= 0 (compare a b)))

(defn between [a b x]
  (and (gt x a)
       (lt x b)))

(defn like [a b]
  (let [pat (re-pattern
             (s/replace b #"\%" "(.+)"))]
    (re-matches pat a)))

(defn- operator-name [x]
  (case x
    :< 'fractl.compiler.rule/lt
    :<= 'fractl.compiler.rule/lteq
    :> 'fractl.compiler.rule/gt
    :>= 'fractl.compiler.rule/gteq
    :<> 'fractl.compiler.rule/neq
    :like 'fractl.compiler.rule/like
    :in 'fractl.compiler.rule/in
    :between 'fractl.compiler.rule/between
    (symbol (name x))))

(defn- accessor-expression
  "Parse the name into a path and return an expression that
  fetches the value at the path from the runtime argument map.
  This expression is used for accessing arguments of the predicate,
  generated by compiling a rule."
  [n]
  (let [[c ref] (li/split-path n)
        parts (li/split-ref (or ref c))
        r (vec (rest parts))
        p (if (and c ref) [c (first parts)] (first parts))]
    (if (seq r)
      `(get-in (~(symbol "-arg-map-") ~p) ~r)
      `(~(symbol "-arg-map-") ~p))))

(defn- fix-operands [attr-name pat]
  (if (<= (count pat) 2)
    [(first pat) attr-name (second pat)]
    pat))

(defn compile-one-rule-pattern
  ([attr-name pat]
   (seq
    (mapv
     (fn [p]
       (cond
         (li/operator? p) (operator-name p)
         (li/name? p) (accessor-expression p)
         (vector? p)
         (if (li/operator? (first p))
           (compile-one-rule-pattern attr-name (fix-operands attr-name p))
           p)
         :else p))
     pat)))
  ([pat]
   (compile-one-rule-pattern nil pat)))

(defn compile-rule-pattern
  "Compile the dataflow match pattern into a predicate"
  ([attr-name pat]
   (let [expr (compile-one-rule-pattern attr-name [pat])
         fexpr `(fn [~(symbol "-arg-map-")]
                  (try
                    ~@expr
                    (catch Exception ex#
                      (log/error
                       (str "failed to execute conditional event predicate"
                            ex#))
                      nil)))]
     (li/evaluate fexpr)))
  ([pat]
   (compile-rule-pattern nil pat)))
