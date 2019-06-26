<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright 2019, California Institute of Technology ("Caltech").
  U.S. Government sponsorship acknowledged.
  
  All rights reserved.
  
  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
  
  * Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
  * Redistributions must reproduce the above copyright notice, this list of
  conditions and the following disclaimer in the documentation and/or other
  materials provided with the distribution.
  * Neither the name of Caltech nor its operating division, the Jet Propulsion
  Laboratory, nor the names of its contributors may be used to endorse or
  promote products derived from this software without specific prior written
  permission.
  
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
  POSSIBILITY OF SUCH DAMAGE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0"
  xmlns:xs="http://www.w3.org/2001/XMLSchema">
  
  <xsl:template match="@xpathDefaultNamespace" />
  
  <xsl:template match="xs:assert" />
  
  <xsl:template match="xs:group[@ref]">
    <xsl:variable name="name" select="@ref" />
    <xsl:apply-templates select="/xs:schema/xs:group[@name = $name]/xs:sequence/*|/xs:schema/xs:group[@name = $name]/xs:all/*" />
  </xsl:template>
  
  <!-- Omit other groups. -->
  <xsl:template match="xs:group[@name]" />
  
  <!-- Convert uses of xs:integer to xs:int, so that we map to simple Java types
       instead of BigDecimal. -->
  <xsl:template match="xs:restriction[@base='xs:integer']">
    <xs:restriction base="xs:int">
      <xsl:apply-templates select="node()" />
    </xs:restriction>
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>