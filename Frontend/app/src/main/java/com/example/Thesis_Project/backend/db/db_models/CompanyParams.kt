package com.example.Thesis_Project.backend.db.db_models
import java.util.Date

data class CompanyParams(
   var leaveleft: Int? = null,
   var maxtotalleaveleft: Int? =null,
   var minimummonthsworked: Int? =null,
   var maxmonthlyleaveleft: Int? = null,
   var wifissid: String? = null,
   var tapintime: String? = null,
   var tapouttime: String? = null,
   var companyworktime: Int? = null,
   var toleranceworktime: Int? = null,
   var maxcompensatetime: Int? = null,
   var maxpermissionsleft: Int? = null
    )
