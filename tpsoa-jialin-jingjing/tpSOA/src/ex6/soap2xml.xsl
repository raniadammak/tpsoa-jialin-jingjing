<?xml version="1.0" encoding="UTF-8" ?> 
<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>	
	<xsl:strip-space elements="*" />
	<xsl:template match="/S:Envelope/S:Body/getPrixResponse">	
		<xsl:element name="prix" >	
    		<xsl:value-of select="." />
    	 </xsl:element>						
	</xsl:template>
</xsl:stylesheet>