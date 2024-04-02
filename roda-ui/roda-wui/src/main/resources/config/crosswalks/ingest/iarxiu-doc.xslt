<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:voc="http://schemas.user.iarxiu.hp.com/2.0/Voc_document"                
                xmlns="http://schemas.user.iarxiu.hp.com/2.0/Voc_document"
                exclude-result-prefixes="voc">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"
                omit-xml-declaration="yes" />

    <xsl:template match="/">
        <doc>
            <xsl:apply-templates />
        </doc>
    </xsl:template>

    <xsl:template match="/voc:document">
        <xsl:message><xsl:value-of select="voc:titol[1]/text()" /></xsl:message>
        <field name="level">
           UDS
        </field>
        <field name="level_txt">
           UDS
        </field>        
        <xsl:if test="count(voc:titol)  &gt; 0">
            <xsl:if test="voc:titol[1]/text()">
                <field name="title">
                    <xsl:value-of select="voc:titol[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:titol">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="title_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:codi_referencia)  &gt; 0">
            <xsl:if test="voc:codi_referencia[1]/text()">
                <field name="codi_referencia">
                    <xsl:value-of select="voc:codi_referencia[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:codi_referencia">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="codi_referencia_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:codi_classificacio)  &gt; 0">
            <xsl:if test="voc:codi_classificacio[1]/text()">
                <field name="codi_classificacio">
                    <xsl:value-of select="voc:codi_classificacio[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:codi_classificacio">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="codi_classificacio_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:titol_serie_documental)  &gt; 0">
            <xsl:if test="voc:titol_serie_documental[1]/text()">
                <field name="titol_serie_documental">
                    <xsl:value-of select="voc:titol_serie_documental[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:titol_serie_documental">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="titol_serie_documental_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:numero_document)  &gt; 0">
            <xsl:if test="voc:numero_document[1]/text()">
                <field name="numero_document">
                    <xsl:value-of select="voc:numero_document[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:numero_document">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="numero_document_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:data_creacio)  &gt; 0">
            <xsl:if test="voc:data_creacio[1]/text()">
                <field name="data_creacio">
                    <xsl:value-of select="voc:data_creacio[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:data_creacio">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="data_creacio_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:nivell_descripcio)  &gt; 0">
            <xsl:if test="voc:nivell_descripcio[1]/text()">
                <field name="nivell_descripcio">
                    <xsl:value-of select="voc:nivell_descripcio[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:nivell_descripcio">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="nivell_descripcio_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:suport)  &gt; 0">
            <xsl:if test="voc:suport[1]/text()">
                <field name="suport">
                    <xsl:value-of select="voc:suport[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:suport">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="suport_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:nom_productor)  &gt; 0">
            <xsl:if test="voc:nom_productor[1]/text()">
                <field name="nom_productor">
                    <xsl:value-of select="voc:nom_productor[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:nom_productor">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="nom_productor_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:unitat_productora)  &gt; 0">
            <xsl:if test="voc:unitat_productora[1]/text()">
                <field name="unitat_productora">
                    <xsl:value-of select="voc:unitat_productora[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:unitat_productora">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="unitat_productora_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:tipus_document)  &gt; 0">
            <xsl:if test="voc:tipus_document[1]/text()">
                <field name="tipus_document">
                    <xsl:value-of select="voc:tipus_document[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:tipus_document">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="tipus_document_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:classificacio_seguretat_acces)  &gt; 0">
            <xsl:if test="voc:classificacio_seguretat_acces[1]/text()">
                <field name="classificacio_seguretat_acces">
                    <xsl:value-of select="voc:classificacio_seguretat_acces[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:classificacio_seguretat_acces">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="classificacio_seguretat_acces_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:nivell_classificacio_evidencial)  &gt; 0">
            <xsl:if test="voc:nivell_classificacio_evidencial[1]/text()">
                <field name="nivell_classificacio_evidencial">
                    <xsl:value-of select="voc:nivell_classificacio_evidencial[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:nivell_classificacio_evidencial">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="nivell_classificacio_evidencial_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:document_essencial)  &gt; 0">
            <xsl:if test="voc:document_essencial[1]/text()">
                <field name="document_essencial">
                    <xsl:value-of select="voc:document_essencial[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:document_essencial">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="document_essencial_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>

