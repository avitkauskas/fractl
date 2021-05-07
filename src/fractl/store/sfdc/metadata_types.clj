(ns fractl.store.sfdc.metadata-types)

(def type-names [:Profile :Role :ApexPage :Settings :CustomObject])

;; Note - some types that can cause errors with deploy/retrieve are
;; commented out.
(def all-type-names
  [:AccountRelationshipShareRule
   :ActionLinkGroupTemplate
   :ActionPlanTemplate
   :AIApplication
   :AIApplicationConfig
   :AnalyticSnapshot
   :AnimationRule
   ;; :ArticleType
   :ApexClass
   :ApexComponent
   :ApexEmailNotifications
   :ApexPage
   :ApexTestSuite
   :ApexTrigger
   :AppMenu
   :AppointmentSchedulingPolicy
   :ApprovalProcess
   :AssignmentRules
   :Audience
   :AuraDefinitionBundle
   :AuthProvider
   :AutoResponseRules
   :BatchCalcJobDefinition
   :BatchProcessJobDefinition
   :BlacklistedConsumer
   :Bot
   :BotVersion
   :BrandingSet
   :BriefcaseDefinition
   :BusinessProcessGroup
   :CallCenter
   :CallCoachingMediaProvider
   :CampaignInfluenceModel
   :CaseSubjectParticle
   :CareSystemFieldMapping
   :CareProviderSearchConfig
   :Certificate
   :ChatterExtension
   :CleanDataService
   :CMSConnectSource
   :Community
   :CommunityTemplateDefinition
   :CommunityThemeDefinition
   ;; :ConnectedApp
   :ContentAsset
   :CorsWhitelistOrigin
   :CspTrustedSite
   :CustomApplication
   :CustomApplicationComponent
   :CustomFeedFilter
   :CustomHelpMenuSection
   :CustomLabels
   :CustomNotificationType
   :CustomObject
   :CustomObjectTranslation
   :CustomPageWebLink
   :CustomPermission
   :CustomSite
   :CustomTab
   ;; :CustomValue
   :Dashboard
   :DataCategoryGroup
   :DataConnectorS3
   :DataSource
   :DataSourceObject
   :DataStreamDefinition
   :DecisionTable
   :DecisionTableDatasetLink
   :DelegateGroup
   :Document
   :DuplicateRule
   :EclairGeoData
   :EmailServicesFunction
   :EmailTemplate
   :EmbeddedServiceBranding
   :EmbeddedServiceConfig
   :EmbeddedServiceFieldService
   :EmbeddedServiceFlowConfig
   :EmbeddedServiceLiveAgent
   :EmbeddedServiceMenuSettings
   :EntitlementProcess
   :EntitlementTemplate
   :EscalationRules
   :EventDelivery
   :EventSubscription
   :ExperienceBundle
   :ExternalDataConnector
   :ExternalDataSource
   :ExternalServiceRegistration
   :FeatureParameterBoolean
   :FeatureParameterDate
   :FeatureParameterInteger
   :FieldSrcTrgtRelationship
   :FlexiPage
   :Flow
   :FlowCategory
   :FlowDefinition
   ;; :Folder
   ;; :GlobalPicklist
   ;; :GlobalPicklistValue
   :GlobalValueSet
   :GlobalValueSetTranslation
   :Group
   :HomePageComponent
   :HomePageLayout
   :InboundCertificate
   :InboundNetworkConnection
   :InstalledPackage
   :KeywordList
   :Layout
   :Letterhead
   :LightningBolt
   :LightningComponentBundle
   :LightningExperienceTheme
   :LightningMessageChannel
   :LightningOnboardingConfig
   :LiveChatAgentConfig
   :LiveChatButton
   :LiveChatDeployment
   :LiveChatSensitiveDataRule
   :ManagedContentType
   :ManagedTopics
   :MatchingRule
   ;; :Metadata
   ;; :MetadataWithContent
   :MilestoneType
   :MlDomain
   :MktDataTranObject
   :MLDataDefinition
   :MLPredictionDefinition
   :MobileApplicationDetail
   :ModerationRule
   :MutingPermissionSet
   :MyDomainDiscoverableLogin
   :NamedCredential
   :NavigationMenu
   :Network
   :NetworkBranding
   :NotificationTypeConfig
   :OauthCustomScope
   :ObjectSourceTargetMap
   :OutboundNetworkConnection
   ;; :Package
   :PathAssistant
   :PaymentGatewayProvider
   :PermissionSet
   :PermissionSetGroup
   :PlatformCachePartition
   :PlatformEventChannel
   :PlatformEventChannelMember
   :PlatformEventSubscriberConfig
   :Portal
   :PostTemplate
   :PresenceDeclineReason
   :PresenceUserConfig
   :Profile
   ;; :ProfileActionOverride
   :ProfilePasswordPolicy
   :ProfileSessionSetting
   :Prompt
   :Queue
   :QueueRoutingConfig
   :QuickAction
   :RedirectWhitelistUrl
   :RecommendationStrategy
   :RecordActionDeployment
   :RemoteSiteSetting
   :Report
   :ReportType
   :Role
   ;; :RoleOrTerritory
   :SalesWorkQueueSettings
   :SamlSsoConfig
   :Scontrol
   :ServiceAISetupDefinition
   :ServiceAISetupField
   :ServiceChannel
   :ServicePresenceStatus
   :Settings
   ;; :SharedTo
   ;; :SharingBaseRule
   :SharingRules
   :SharingSet
   :SiteDotCom
   :Skill
   :StandardValueSet
   :StandardValueSetTranslation
   :StaticResource
   :SynonymDictionary
   :Territory
   :Territory2
   :Territory2Model
   :Territory2Rule
   :Territory2Type
   :TimeSheetTemplate
   :TopicsForObjects
   :TransactionSecurityPolicy
   :Translations
   :UserAuthCertificate
   :UserCriteria
   :UserProvisioningConfig
   :WaveApplication
   :WaveDataflow
   :WaveDashboard
   :WaveDataset
   :WaveLens
   :WaveRecipe
   :WaveTemplateBundle
   :WaveXmd
   :WebStoreTemplate
   :Workflow
   :WorkSkillRouting])

(defn built-in-type [n]
  (some #{n} type-names))