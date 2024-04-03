<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:voc="http://schemas.user.iarxiu.hp.com/2.0/Voc_expedient"
                xmlns="http://schemas.user.iarxiu.hp.com/2.0/Voc_expedient"
                exclude-result-prefixes="voc">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"
                omit-xml-declaration="yes" />
    <xsl:param name="i18n.title"/>
    <xsl:param name="i18n.codi_referencia" />
    <xsl:param name="i18n.titol_serie_documental"/>
    <xsl:param name="i18n.numero_expedient"/>
    <xsl:param name="i18n.codi_classificacio"/>
    <xsl:param name="i18n.data_obertura"/>
    <xsl:param name="i18n.data_tancament"/>
    <xsl:param name="i18n.nivell_descripcio"/>
    <xsl:param name="i18n.nom_productor"/>
    <xsl:param name="i18n.unitat_productora"/>
    <xsl:param name="i18n.descripcio"/>
    <xsl:param name="i18n.descriptors"/>
    <xsl:param name="i18n.classificacio_seguretat_acces"/>
    <xsl:param name="i18n.sensibilitat_dades_lopd"/>
    
    <xsl:template match="/">
        <div class="descriptiveMetadata">
            <xsl:apply-templates />
        </div>
    </xsl:template>
    <xsl:template match="/voc:expedient">
        <xsl:if test="voc:titol">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:titol">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.title"/>
                    </div>
                    <xsl:for-each select="voc:titol">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:codi_referencia">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:codi_referencia">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.codi_referencia"/>
                    </div>
                    <xsl:for-each select="voc:codi_referencia">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:numero_expedient">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:numero_expedient">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.numero_expedient"/>
                    </div>
                    <xsl:for-each select="voc:numero_expedient">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:codi_classificacio">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:codi_classificacio">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.codi_classificacio"/>
                    </div>
                    <xsl:for-each select="voc:codi_classificacio">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:titol_serie_documental">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:titol_serie_documental">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.titol_serie_documental"/>
                    </div>
                    <xsl:for-each select="voc:titol_serie_documental">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:nivell_descripcio">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:nivell_descripcio">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.nivell_descripcio"/>
                    </div>
                    <xsl:for-each select="voc:nivell_descripcio">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:data_obertura">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:data_obertura">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.data_obertura"/>
                    </div>
                    <xsl:for-each select="voc:data_obertura">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:data_tancament">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:data_tancament">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.data_tancament"/>
                    </div>
                    <xsl:for-each select="voc:data_tancament">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:nom_productor">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:nom_productor">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.nom_productor"/>
                    </div>
                    <xsl:for-each select="voc:nom_productor">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:unitat_productora">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:unitat_productora">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.unitat_productora"/>
                    </div>
                    <xsl:for-each select="voc:unitat_productora">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:descripcio">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:descripcio">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.descripcio"/>
                    </div>
                    <xsl:for-each select="voc:descripcio">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:descriptors">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:descriptors">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.descriptors"/>
                    </div>
                    <xsl:for-each select="voc:descriptors">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:classificacio_seguretat_acces">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:classificacio_seguretat_acces">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.classificacio_seguretat_acces"/>
                    </div>
                    <xsl:for-each select="voc:classificacio_seguretat_acces">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
        <xsl:if test="voc:sensibilitat_dades_LOPD">
            <xsl:variable name="joinedText">
                <xsl:for-each select="voc:sensibilitat_dades_LOPD">
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:if test="normalize-space($joinedText) != ''">
                <div class="field">
                    <div class="label">
                        <xsl:value-of select="$i18n.sensibilitat_dades_lopd"/>
                    </div>
                    <xsl:for-each select="voc:sensibilitat_dades_LOPD">
                        <div class="value">
                            <xsl:value-of select="text()" />
                        </div>
                    </xsl:for-each>
                </div>
            </xsl:if>
        </xsl:if>        
    </xsl:template>
</xsl:stylesheet>


