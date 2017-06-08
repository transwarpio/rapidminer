package io.transwarp.midas.constant.midas

object DataTypes {
  // data types should not depend on spark
  val SHORT = "short" // ShortType.typeName
  val LONG = "long" // LongType.typeName
  val INT = "integer" // IntegerType.typeName
  val FLOAT = "float" // FloatType.typeName
  val DOUBLE = "double" // DoubleType.typeName
  val STRING = "string" // StringType.typeName
  val BOOL = "boolean" // BooleanType.typeName
  val BINARY = "binary" // BinaryType.typeName
  val VECTOR = "vector" // VectorUDT.typeName
  val DATE = "date"
  val DECIMAL = "decimal"
  val TIMESTAMP = "timestamp"
  val STRUCT = "struct"
}

object DataNames {
  val association = "association"
  val dataSet = "dataset"
  val Parameters = "parameters"
  val Thresholds = "thresholds"
  val statistics = "statistics"
}

object ColumnMeta {
  val Role = "role"
  val Attribute = "attribute"
}

object ColumnTypes {
  val Binary = "binary"
  val Numeric = "numeric"
  val Nominal = "nominal"
  val Integer = "integer"
}

object ColumnRoles {
  val ID = "id"
  val LABEL = "label"
  val PREDICTION = "prediction"
  val FEATURE = "feature"
  val CENSOR = "censor"
}

object ColumnNames {
  val Label = "label"
  val IndexedLabel = "indexedLabel"
  val Feature = "feature"
  val Features = "features"
  val Prediction = "prediction"
  val IndexedPrediction = "indexedPrediction"
  val Threshold = "threshold"
  val PredictionLabel = "predictionLabel"
  val RawPrediction = "rawPrediction"
  val Probability = "probability"
  val Probabilities = "probabilities"
  val Censor = "censor"
  val IndexedFeatures = "indexedFeatures"

  // for association
  val Transaction = "transaction"
  val FreqItemset = "freqitemset"
  val Count = "count"
  val Antecedent = "antecedent"
  val Consequent = "consequent"
  val Confidence = "confidence"
  val Sequence = "sequence"
  val Recommend = "recommend"
}

object PropertyKey {
  val propertyRole = "role"
  val propertyType = "type"
}
