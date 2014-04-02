<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
    version="1.1" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:util="org.epo.dataresources.csm.web.transform.TransformUtils"
    exclude-result-prefixes="util">

	<xsl:output method="html" indent="no" />

	<xsl:template match="div[@class='loop']">
		<loop>
			<xsl:apply-templates />
		</loop>
	</xsl:template>
	
	<xsl:template match="div[@class='header']">
		<header>
			<xsl:apply-templates />
		</header>
	</xsl:template>

	<xsl:template match="div[@class='body']">
		<body>
			<xsl:apply-templates />
		</body>
	</xsl:template>

	<xsl:template match="div[@class='footer']">
		<footer>
			<xsl:apply-templates />
		</footer>
	</xsl:template>
	
	<xsl:template match="span[@class='hashtag']">
		<xsl:element name="tag">
			<xsl:attribute name="type">hashtag</xsl:attribute>
			<xsl:apply-templates />
			
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="span[@class='usertag']">
		<xsl:element name="tag">
			<xsl:attribute name="type">usertag</xsl:attribute>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	
	<xsl:template match="p"><p><xsl:apply-templates /></p> </xsl:template>
	<xsl:template match="b"><b><xsl:apply-templates /></b> </xsl:template>
	<xsl:template match="i"><i><xsl:apply-templates /></i> </xsl:template>
    
</xsl:stylesheet>
