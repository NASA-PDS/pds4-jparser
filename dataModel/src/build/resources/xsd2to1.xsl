<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0"
  xmlns:xs="http://www.w3.org/2001/XMLSchema">
  
  <xsl:template match="@xpathDefaultNamespace" />
  
  <xsl:template match="xs:assert" />
  
  <xsl:template match="xs:group[@ref]">
    <xsl:variable name="name" select="@ref" />
    <xsl:apply-templates select="/xs:schema/xs:group[@name = $name]/xs:sequence/*|/xs:schema/xs:group[@name = $name]/xs:all/*" />
  </xsl:template>
  
  <xs:template match="xs:group[@name]" />
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>