package de.mathism.opensky.api

import com.google.inject.Inject
import de.mathism.abstractions.http.BaseHttpClient
import de.mathism.opensky.api.di.OpenskyBaseUrl


class BaseOpenskyApiClient @Inject constructor(@OpenskyBaseUrl override var baseUri: String) : BaseHttpClient()