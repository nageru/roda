<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:voc="http://schemas.user.iarxiu.hp.com/2.0/Voc_expedient"
                xmlns="http://schemas.user.iarxiu.hp.com/2.0/Voc_expedient"                     
                exclude-result-prefixes="voc">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"
                omit-xml-declaration="yes" />

    <xsl:template match="/">
        <doc>
            <xsl:apply-templates />
        </doc>
    </xsl:template>

    <xsl:template match="/voc:expedient">
        <xsl:message><xsl:value-of select="voc:titol[1]/text()" /></xsl:message>
        <field name="level">
           UDC
        </field>
        <field name="level_txt">
           UDC
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
        <xsl:if test="count(voc:numero_expedient)  &gt; 0">
            <xsl:if test="voc:numero_expedient[1]/text()">
                <field name="numero_expedient">
                    <xsl:value-of select="voc:numero_expedient[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:numero_expedient">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="numero_expedient_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:data_obertura)  &gt; 0">
            <xsl:if test="voc:data_obertura[1]/text()">
                <field name="data_obertura">
                    <xsl:value-of select="voc:data_obertura[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:data_obertura">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="data_obertura_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="voc:data_tancament  &gt; 0">
            <xsl:if test="voc:data_tancament[1]/text()">
                <field name="data_tancament">
                    <xsl:value-of select="voc:data_tancament[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:data_tancament">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="data_tancament_txt">
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
                    <field name="voc:nivell_descripcio_txt">
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
        <xsl:if test="count(voc:sensibilitat_dades_LOPD)  &gt; 0">
            <xsl:if test="voc:sensibilitat_dades_LOPD[1]/text()">
                <field name="sensibilitat_dades_LOPD">
                    <xsl:value-of select="voc:sensibilitat_dades_LOPD[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:sensibilitat_dades_LOPD">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="sensibilitat_dades_LOPD_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(voc:descripcio)  &gt; 0">
            <xsl:if test="voc:descripcio[1]/text()">
                <field name="descripcio">
                    <xsl:value-of select="voc:descripcio[1]/text()" />
                </field>
            </xsl:if>
            <xsl:for-each select="voc:descripcio">
                <xsl:if test="normalize-space(text())!=''">
                    <field name="descripcio_txt">
                        <xsl:value-of select="text()" />
                    </field>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>

