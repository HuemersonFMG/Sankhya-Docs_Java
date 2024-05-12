if ($col_valorcampo == null || $col_valorcampo == 0) {
    return $col_VLRVENDA.doubleValue() - ($col_VLRVENDA.doubleValue() * ($col_PERDESCCOM != null ? $col_PERDESCCOM.doubleValue() : 0.0) / 100.0);
}

if ($valorCampo != null) {
    return $valorCampo;
}
return null;


#type.sql#
SELECT PRO.REFFORN FROM TGFPRO PRO WHERE PRO.CODPROD = AD_CONTPRODD.AD_QTD