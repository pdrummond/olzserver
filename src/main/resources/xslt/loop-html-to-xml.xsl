<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
    version="1.1" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:util="org.epo.dataresources.csm.web.transform.TransformUtils"
    exclude-result-prefixes="util">

	<xsl:output method="xml" indent="no"/>

	<xsl:template match="div[@class='loop']">
		<loop>
			<xsl:apply-templates />
		</loop>
	</xsl:template>
	
	<xsl:template match="div[@class='body']">
		<body>
			<xsl:apply-templates />
		</body>
	</xsl:template>

	<xsl:template match="div[@class='tags-box']">
		<tags-box>
			<xsl:apply-templates />
		</tags-box>
	</xsl:template>
	
	<xsl:template match="span[@class='hashtag']">
		<xsl:element name="tag">
			<xsl:attribute name="type">hashtag</xsl:attribute>
			<xsl:apply-templates />
			
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="a[@data-type='loop-ref']">
		<xsl:element name="loop-ref">
			<xsl:value-of select="."></xsl:value-of>
		</xsl:element>
	</xsl:template>
	
	
	<xsl:template match="p"><p><xsl:apply-templates /></p> </xsl:template>
	<xsl:template match="b"><b><xsl:apply-templates /></b> </xsl:template>
	<xsl:template match="i"><i><xsl:apply-templates /></i> </xsl:template>
	<xsl:template match="h1"><h1><xsl:apply-templates /></h1> </xsl:template>
	<xsl:template match="h2"><h2><xsl:apply-templates /></h2> </xsl:template>
	<xsl:template match="h3"><h3><xsl:apply-templates /></h3> </xsl:template>
    
</xsl:stylesheet>
