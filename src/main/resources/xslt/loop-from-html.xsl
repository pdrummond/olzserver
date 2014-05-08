<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
    version="1.1" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:util="org.epo.dataresources.csm.web.transform.TransformUtils"
    exclude-result-prefixes="util">

	<xsl:output method="text" indent="no"/>

	<xsl:template match="root">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="p">
		<xsl:apply-templates />


	</xsl:template>
	<xsl:template match="b">**<xsl:apply-templates />**</xsl:template>
	<xsl:template match="em">*<xsl:apply-templates />*</xsl:template>
	<xsl:template match="h1"># <xsl:apply-templates /> </xsl:template>
	<xsl:template match="h2">## <xsl:apply-templates /> </xsl:template>
	<xsl:template match="h3">### <xsl:apply-templates /> </xsl:template>
	<xsl:template match="br">
		<xsl:apply-templates /> 
		
	</xsl:template>
	<xsl:template match="ul"><xsl:apply-templates /></xsl:template>
	<xsl:template match="li">* <xsl:apply-templates /> </xsl:template>
</xsl:stylesheet>
