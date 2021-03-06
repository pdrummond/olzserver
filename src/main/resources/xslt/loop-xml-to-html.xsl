<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
    version="1.1" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:util="org.epo.dataresources.csm.web.transform.TransformUtils"
    exclude-result-prefixes="util">

	<xsl:output method="html" indent="no"/>

	<xsl:template match="loop">
		<div class="loop">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="body">
		<div class="body">
        	<xsl:apply-templates />
        </div>
    </xsl:template>

	<xsl:template match="tags-box">
		<div class="tags-box">
        	<xsl:apply-templates />
        </div>
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
	
	
	<xsl:template match="tag[@type='hashtag']">
		<xsl:element name="a">
			<xsl:attribute name="class">hashtag</xsl:attribute>
			<xsl:attribute name="href">/#loop/<xsl:value-of select="."></xsl:value-of></xsl:attribute>
			<xsl:value-of select="."></xsl:value-of>			
		</xsl:element>
	</xsl:template>

	<xsl:template match="loop-ref">
		<xsl:element name="a">
			<xsl:attribute name="class">loop-ref</xsl:attribute>
			<xsl:attribute name="data-type">loop-ref</xsl:attribute>
			<xsl:attribute name="href">/#loop/<xsl:value-of select="."></xsl:value-of></xsl:attribute>
			<xsl:value-of select="."></xsl:value-of>			
		</xsl:element>
	</xsl:template>

    
</xsl:stylesheet>
