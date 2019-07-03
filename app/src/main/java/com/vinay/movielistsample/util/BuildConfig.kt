package com.vinay.movielistsample.util

import com.vinay.movielistsample.BuildConfig

const val PROD_FLAVOR = "prod"
const val TESTING_FLAVOR = "dev"
const val STAGING_FLAVOR = "staging"

val isProduction
    get() = BuildConfig.FLAVOR == PROD_FLAVOR

val isTesting
    get() = BuildConfig.FLAVOR == TESTING_FLAVOR

val isStaging
    get() = BuildConfig.FLAVOR == STAGING_FLAVOR
