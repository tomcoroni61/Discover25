package trust.jesus.discover.bible.dataclasses

data class SsBibel(
    val name: String,
    val shortname: String,
    val module: String,
    val year: String,
    val description: String,
    val lang: String,
    val lang_short: String,
    val copyright: Int,
    val italics: Int,
    val strongs: Int,
    val red_letter: Int,
    val paragraph: Int,
    val rank: Int,
    val research: Int,
    val restrict: Int,
    val copyright_id: Int,
    val copyright_statement: String,
    val rtl: Int,
    val lang_native: String,
    val downloadable: Boolean
)
/*
package com.example.example

import com.google.gson.annotations.SerializedName


data class Kjv (

  @SerializedName("name"                ) var name               : String?  = null,
  @SerializedName("shortname"           ) var shortname          : String?  = null,
  @SerializedName("module"              ) var module             : String?  = null,
  @SerializedName("year"                ) var year               : String?  = null,
  @SerializedName("owner"               ) var owner              : String?  = null,
  @SerializedName("description"         ) var description        : String?  = null,
  @SerializedName("lang"                ) var lang               : String?  = null,
  @SerializedName("lang_short"          ) var langShort          : String?  = null,
  @SerializedName("copyright"           ) var copyright          : Int?     = null,
  @SerializedName("italics"             ) var italics            : Int?     = null,
  @SerializedName("strongs"             ) var strongs            : Int?     = null,
  @SerializedName("red_letter"          ) var redLetter          : Int?     = null,
  @SerializedName("paragraph"           ) var paragraph          : Int?     = null,
  @SerializedName("rank"                ) var rank               : Int?     = null,
  @SerializedName("research"            ) var research           : Int?     = null,
  @SerializedName("restrict"            ) var restrict           : Int?     = null,
  @SerializedName("copyright_id"        ) var copyrightId        : Int?     = null,
  @SerializedName("copyright_statement" ) var copyrightStatement : String?  = null,
  @SerializedName("rtl"                 ) var rtl                : Int?     = null,
  @SerializedName("lang_native"         ) var langNative         : String?  = null,
  @SerializedName("downloadable"        ) var downloadable       : Boolean? = null

)
 */