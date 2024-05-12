if ($col_QUANTIDADE2 == null)
return $col_QTDDESC;

import java.math.BigDecimal;

$sql.setParam(“CODPROD”, $col_CODPROD);
$sql.setParam(“QUANTIDADE2”, $col_QUANTIDADE2);

$sql.select(“ROUND( {QUANTIDADE2} / (P1.PESOBRUTO - CASE WHEN P1.USOPROD = ‘E’ THEN 0 ELSE P1.PESOLIQ END), 0) AS PESOEMB”,
“TGFPRO P1”,
"P1.CODPROD = {CODPROD} ");

if( $sql.next() ){
return $sql.getBigDecimal(1);
}

return BigDecimal.ZERO;

/**
     #type.sql# 
    SELECT MAX(EST.CODLOCAL)
    FROM TGFCAB CAB
    INNER JOIN TGFITE ITE ON ITE.NUNOTA = CAB.NUNOTA
    INNER JOIN TGFPRO PRO ON PRO.CODPROD = ITE.CODPROD
    INNER JOIN TGFEST EST ON EST.CODLOCAL = ITE.CODLOCALORIG
    WHERE CAB.NUNOTA = TGFCAB.NUNOTA
 * / */