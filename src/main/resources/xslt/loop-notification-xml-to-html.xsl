<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
    version="1.1" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:util="org.epo.dataresources.csm.web.transform.TransformUtils"
    exclude-result-prefixes="util">

	<xsl:output method="html" indent="no"/>

	<xsl:template match="loop">
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="loop-header">
		<span class='notification'>
			<xsl:apply-templates />
		</span>
   </xsl:template>
      
	<!-- 	<xsl:template match="tag">
		<xsl:element name="span">
			<xsl:attribute name="data-type"><xsl:value-of select="@type"/> </xsl:attribute>
			<xsl:attribute name="class">tag <xsl:value-of select="@type"/></xsl:attribute>
			<xsl:attribute name="data-tag-type">tag <xsl:value-of select="@type"/></xsl:attribute>
			<xsl:element name="a">
				<xsl:attribute name="href">/#query/<xsl:value-of select="."></xsl:value-of></xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template> -->   
</xsl:stylesheet>
