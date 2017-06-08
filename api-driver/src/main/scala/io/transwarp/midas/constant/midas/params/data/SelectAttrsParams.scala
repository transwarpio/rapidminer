package io.transwarp.midas.constant.midas.params.data

object SelectAttrsParams {
  val AttributeFilterType = "attributeFilterType"
  val Attribute = "attribute"
  val Attributes = "attributes"
  val UseExceptionExp = "useExceptExpression"
  val UseValueTypeExp = "useValueTypeException"
  val UseBlockTypeExp = "useBlockTypeException"
  val UseSpecialAttrExp = "includeSpecialAttributes"
  val ExceptRegularExp = "exceptRegularExpression"
  val InvertSelection = "invertSelection"

  val Single = "single"
  val All = "all"
  val Subset = "subset"
  val RegularExp = "regularExpression"

  val Types = Array(
    Single,
    All,
    Subset,
    RegularExp
  )
}
