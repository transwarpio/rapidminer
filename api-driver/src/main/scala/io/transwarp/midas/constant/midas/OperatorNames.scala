package io.transwarp.midas.constant.midas

object OperatorNames {
  // midas specific operators
  val Process = "process"
  val SubProcess = "subprocess"
  val WorkFlowNode = "workflow_node"
  val Retrieve = "retrieve"
  val RetrieveCSV = "retrieve_csv"
  val RetrieveLocalCSV = "retrieve_local_csv"
  val RetrieveJson = "retrieve_json"
  val RetrieveJDBC = "read_database"
  val File = "file"
  val Csv = "csv"
  val Json = "json"
  val Text = "text"
  val DB = "jdbc"
  val HDFS = "hdfs"
  val Inceptor = "inceptor"
  val ApplyModel = "apply_model"
  val ApplyThreshold = "apply threshold"
  val CreateThreshold = "create threshold"
  val FindThreshold = "find threshold"

  // association
  val FPGrowth = "fpgrowth"
  val AssociationRuleGenerator = "association rule generator"
  val PrefixSpan = "prefix_span"
  val Apriori = "apriori"
  val ApplyAssociationRules = "apply_association_rules"
  val ReshapeData = "reshape_data"

  // clustering
  val KMeans = "k_means"
  val LDA = "LDA"
  val BisectingKMeans = "bisecting_k_means"
  val GaussianMixture = "gaussian_mixture"

  // classification
  val LogisticRegressionClassifier = "logistic regression"
  val DecisionTreeClassifier = "decision_tree"
  val RandomForestClassifier = "random_forest"
  val GradientBoostedTreeClassifier = "gradient-boosted tree"
  val MultiLayerPerceptronClassifier = "multilayer perceptron"
  val OneVsRestClassifier = "one vs rest"
  val NaiveBayesClassifier = "naive_bayes"
  val SVM = "support_vector_machine_linear"
  val KNNClassifier = "k_nearest_neighbors"
  val LogisticRegressionMultiClassifier = "logistic regression muticlass classifier"
  val BoostMultiClassClassifier = "boost multi class classifier"

  // regression
  val LinearRegression = "linear regression"
  val DecisionTreeRegression = "decision tree regression"
  val RandomForestRegression = "random forest regression"
  val GradientBoostedTreeRegression = "gradient-boosted tree regression"
  val IsotonicRegression = "isotonic_regression"
  val AFTSurvivalRegression = "aft_survival_regression"
  val GeneralizedLinearRegression = "generalized_linear_regression"

  // feature extractor
  val TF = "tf"
  val IDF = "idf"
  val Word2Vec = "word to vector"
  val CountVectorizer = "count_vectorizer"

  // feature transformer
  val Tokenizer = "tokenizer"
  val StopWordsRemover = "stop word remover"
  val NGram = "n gram"
  val Binarizer = "binarizer"
  val PCA = "PCA"
  val PolynomialExpansion = "polynominal expansion"
  val DiscreteCosineTransform = "discrete_consine_transform"
  val StringIndexer = "string_indexer"
  val IndexToString = "index to string"
  val OneHotEncoder = "one_hot_encoder"
  val VectorIndexer = "vector_indexer"
  val Normalizer = "normalizer"
  val StandardScaler = "Standard scaler"
  val MinMaxScaler = "min max scaler"
  val MaxAbsScaler = "max abs scaler"
  val Bucketizer = "bucketizer"
  val ElementwiseProduct = "element wise product"
  val SQLTransformer = "sql_transformer"
  val VectorAssembler = "vector assembler"
  val QuantileDiscretizer = "quantile_discretizer"
  val DataTypeTransformer = "data_type_transformer"
  val GenerateIDFeature = "generate_id_feature"
  val OutlierSoften = "outlier_soften"
  val WindowVariableStat = "window_variable_stat"

  val TruncatedSVD = "truncated SVD"

  // feature selector
  val CalculateWeight = "calculate_weight"
  val SelectFeatureByWeight = "select_by_weights"
  val VectorSlicer = "vector slicer"
  val RFormula = "R Formula"

  // evaluator
  val BinEval = "binary classification evaluate"
  val MultiEval = "multi classification evaluate"
  val RegressionEval = "regression evaluation"
  val ClusterEval = "cluster evaluation"

  // model selector
  val CrossValidate = "cross validation"
  val GridSearch = "grid search"

  // recommendation
  val ALS = "als"
  val FactorizationMachines = "factorization_machines"

  // read
  val ReadModel = "read model"
  val ReadModelPMML = "read_model_pmml"

  // write
  val WriteModel = "write model"
  val WriteModelPMML = "write_model_pmml"
  val WriteTable = "write table"
  val WriteJdbc = "write_jdbc"
  val WriteFile = "write_file"
  val WriteToCSV = "write_to_csv"
  val WriteToJson = "write_to_json"
  val ModelToJson = "model_to_json"

  // attribute operators
  val SetRole = "set_role"
  val ScaleTransform = "scale_transform"
  val Rename = "rename"
  val RenameByReplace = "rename_by_replacing"
  val FilterData = "filter_data"
  val SelectAttributes = "select_attributes"
  val Transpose = "transpose"
  val ReplaceMissing = "replace missing"

  // sql-like operators
  val Select = "select"

  // agg operators
  val Aggregate = "aggregate"
  val GroupBy = "groupby"
  val Cube = "cube"
  val Rollup = "rollup"
  val Pivot = "pivot"

  val Sample = "sample"
  val SampleExact = "sample exact"
  val OverSample = "over sample"
  val UnderSample = "under sample"
  val BalancedSample = "BalancedSample"
  val MultiplyData = "multiply data"
  val SplitData = "split"

  val Count = "count"
  val Dereplication = "dereplication"
  val Distinct = "distinct"
  val DropDuplicates = "drop_duplicates"
  val OrderBy = "order_by"
  val Top = "top"
  val SetMinus = "set_minus"
  val Interset = "intersect"
  val Join = "join"
  val Union = "union"
  val ReplaceData = "replace_data"

  val CholeskyDecomposition = "cholesky_decomposition"
  val UnivariateFeatureStatistic = "univariate_feature_statistic"

  val BivariateScaleVSScaleFeatureStatistic = "bivariate_scale_vs_scale_feature_statistic"
  val BivariateCateVSCateFeatureStatistic = "bivariate_cate_vs_cate_feature_statistic"
  val BivariateCateVSScaleFeatureStatistic = "bivariate_cate_vs_scale_feature_statistic"

  // random
  val RandomGenerate = "random_generate"

  // deep learning layers
  val DenseLayer = "dense_layer"
  val DropoutLayer = "dropout_layer"
  val OutputLayer = "output_layer"
  val RNNCell = "rnn_cell"
  val BasicRNNCell = "basic_rnn_cell"
  val GRUCell = "gru_cell"
  val BasicLSTMCell = "basic_lstm_cell"
  val MultipleRNNCell = "multiple_rnn_cell"
  val CombinedRNNCells = "combined_rnn_cells"
  val ApplyDeepModel = "apply_deep_model"
  val WideAndDeepLearning = "wide_and_deep_learning"

  // sort
  val MultiColOrderBy = "multi_order_by"

  // deep learning networks
  val ArtificialNeuralNetwork = "artificial_neural_network"

  // nlp related
  val NWI = "nwi"
  val WordSegmentation = "word_segmentation"
  val ApplyWord2Vec = "apply_word2vec"
  val GenDocVec = "gen_doc_vec"

  val LOF = "lof"

  // custom
  val Custom = "custom"
  val Python = "python"

  // macro
  val AddMacro = "add_macro"
  val RemoveMacro = "remove_macro"
  val ExtractMacro = "extract_macro"
  val GenerateMacro = "generate_macro"

  // control
  val LoopByColumns = "loop_by_columns"
  val Loop = "loop"
}

