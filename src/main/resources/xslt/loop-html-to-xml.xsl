<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
    version="1.1" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:util="org.epo.dataresources.csm.web.transform.TransformUtils"
    exclude-result-prefixes="util">

	<xsl:output method="xml" indent="no"/>

	<xsl:template match="div[@data-type='loop']">
		<loop>
			<xsl:apply-templates />
		</loop>
	</xsl:template>
	
	<xsl:template match="div[@data-type='loop-header']">
		<loop-header>
			<xsl:apply-templates />
		</loop-header>
	</xsl:template>

	<xsl:template match="div[@data-type='loop-body']">
		<loop-body>
			<xsl:apply-templates />
		</loop-body>
	</xsl:template>


	<xsl:template match="div[@data-type='loop-footer']">
		<loop-footer>
			<xsl:apply-templates />
		</loop-footer>
	</xsl:template>
	
	<xsl:template match="span[@data-type='tag']">
		<xsl:element name="tag">
			<xsl:attribute name="type"><xsl:value-of select="@data-tag-type"/> </xsl:attribute>
			<xsl:value-of select="."/>			
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="p"><p><xsl:apply-templates /></p> </xsl:template>
	<xsl:template match="b"><b><xsl:apply-templates /></b> </xsl:template>
	<xsl:template match="i"><i><xsl:apply-templates /></i> </xsl:template>
	<xsl:template match="h1"><h1><xsl:apply-templates /></h1> </xsl:template>
	<xsl:template match="h2"><h2><xsl:apply-templates /></h2> </xsl:template>
	<xsl:template match="h3"><h3><xsl:apply-templates /></h3> </xsl:template>
	<xsl:template match="br"><br><xsl:apply-templates /></br> </xsl:template>
	<xsl:template match="ul"><ul><xsl:apply-templates /></ul> </xsl:template>
	<xsl:template match="li"><li><xsl:apply-templates /></li> </xsl:template>
	
    
</xsl:stylesheet>
