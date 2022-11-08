package us.berkovitz.plexapi.myplex

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@SerialName("error")
data class Error(
	@XmlValue(true) val message: String
)

@Serializable
@SerialName("errors")
data class Errors(
	@XmlElement(true) val errors: List<Error>
)
