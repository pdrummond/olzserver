<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
    version="1.1" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:util="org.epo.dataresources.csm.web.transform.TransformUtils"
    exclude-result-prefixes="util">

	<xsl:output method="html" indent="no"/>

	<xsl:template match="loop">
		<div data-type="loop" class="loop">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="loop-header">
		<div data-type="loop-header" class="loop-header">
        	<xsl:apply-templates />
        </div>
    </xsl:template>

	<xsl:template match="loop-body">
		<div data-type="loop-body" class="loop-body">
        	<xsl:apply-templates />
        </div>
    </xsl:template>

	<xsl:template match="loop-footer">
		<div data-type="loop-footer" class="loop-footer">
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
	
	<xsl:template match="tag">
		<xsl:element name="span">
			<xsl:attribute name="data-type"><xsl:value-of select="@type"/> </xsl:attribute>
			<xsl:attribute name="class">tag <xsl:value-of select="@type"/></xsl:attribute>
			<xsl:attribute name="data-tag-type">tag <xsl:value-of select="@type"/></xsl:attribute>
			<xsl:element name="a">
				<xsl:attribute name="href">/#loop/<xsl:value-of select="."></xsl:value-of></xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
