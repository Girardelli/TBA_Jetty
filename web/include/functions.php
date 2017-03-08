<?php

// +----------------------------------------------------------
// | DATA
// +----------------------------------------------------------

function current_url() {
    $pageURL = 'http';
    if ($_SERVER["HTTPS"] == "on") {
        $pageURL .= "s";
    }
    $pageURL .= "://";
    if ($_SERVER["SERVER_PORT"] != "80") {
        $pageURL .= $_SERVER["SERVER_NAME"] . ":" . $_SERVER["SERVER_PORT"] . $_SERVER["REQUEST_URI"];
    } else {
        $pageURL .= $_SERVER["SERVER_NAME"] . $_SERVER["REQUEST_URI"];
    }
    return $pageURL;
}

function getMonth($month) {
    $months = array("januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november", "december");
    return $months[$month - 1];
}

function getBestandPad($bestandId) {
    global $bestandPad;
    $blBestand = new BLBestand();
    $blObject = new BLObject();
    $bestand = $blBestand->getBestand($bestandId);
    $object = $blObject->getObject($bestand->getObject());
    return $bestandPad . $object->getMap() . "/" . $bestand->getNaam();
}

function iterate_cats_select($categorieen, $subs, $current_catId, $curr_parentCatId) {
    $blCategorie = new BLCategorie();
    for ($i = 0; $i < count($categorieen); $i++) {
        if ($current_catId != $categorieen[$i]->getId()) {
            $selected = "";
            if ($curr_parentCatId == $categorieen[$i]->getId()) {
                $selected = "selected";
            }
            echo '<option value="' . $categorieen[$i]->getId() . '" ' . $selected . '>
                    ' . $subs . $categorieen[$i]->getNaam() . '
                </option>';
        }
        iterate_cats_select($blCategorie->getCategoriesByPara("parentCategorieId", $categorieen[$i]->getId()), "&nbsp;&nbsp;&nbsp;" . $subs . "-", $current_catId, $curr_parentCatId);
    }
}

function showParentCats($catId, $division, $makeLink = false) {
    $blCategorie = new BLCategorie();
    $categorie = $blCategorie->getCategorie($catId);
    if ($categorie->getParentCategorieId() != null) {
        $parent = $blCategorie->getCategorie($categorie->getParentCategorieId());
    } else {
        $parent = null;
    }
    $cats = $categorie->getNaam();
    if ($makeLink) {
        $cats = makeLink($categorie->getNaam(), "");
    }

    while ($parent != null) {
        $parentNaam = $parent->getNaam();
        if ($makeLink) {
            $parentNaam = makeLink($parent->getNaam(), "");
        }
        $cats = $parentNaam . $division . $cats;
        if ($parent->getParentCategorieId() != null) {
            $parent = $blCategorie->getCategorie($parent->getParentCategorieId());
        } else {
            $parent = null;
        }
    }
    return $cats;
}

// +----------------------------------------------------------
// | LINK
// +----------------------------------------------------------
function redirect($location) {
    header("Location: $location");
    die();
}

function sendmail($fromNaam, $fromEmail, $toNaam, $toEmail, $replytoNaam, $replytoEmail, $onderwerp, $bericht, $attachments = array()) {
    $mail = new phpmailer(); //defaults to using php "mail()"; the true param means it will throw exceptions on errors, which we need to catch

    $mail->AddReplyTo($replytoEmail, $replytoNaam);
    $mail->AddAddress($toEmail, $toNaam);
    $mail->From = $fromEmail;
    $mail->FromName = $fromNaam;

    $mail->Subject = $onderwerp;

    $mail->MsgHTML($bericht); // body

    foreach ($attachments as $attachment) {
        $mail->AddAttachment($attachment); // attach uploaded file
    }

    $mail->Send();
    return true;
}

// +----------------------------------------------------------
// | TOEGANG FUNCTIES
// +----------------------------------------------------------
function gebruiker() {
    if (isset($_SESSION["gebruiker"])) {
        return true;
    } else {
        return false;
    }
}

function logged_in() {
    if (isset($_SESSION["login"])) {
        return true;
    } else {
        return false;
    }
}

// +----------------------------------------------------------
// | STRING FUNCTIES
// +----------------------------------------------------------
// enkele quotes escapen als get_magic_quotes_gpc niet op staat (van 't Seyen naar \'t Seyen)
function escape($input) {
    $con = mysqli_connect(HOST, USERNAME, PASSWORD);
    if (get_magic_quotes_gpc($input)) {
        $input = stripslashes($input);
    }
    $input = mysqli_real_escape_string($con, $input);

    return $input;
}

function makeLink($string, $division = "-") {
    return strtolower(trim(preg_replace('~[^0-9a-z]+~i', $division, html_entity_decode(preg_replace('~&([a-z]{1,2})(?:acute|cedil|circ|grave|lig|orn|ring|slash|th|tilde|uml);~i', '$1', htmlentities($string, ENT_QUOTES, 'UTF-8')), ENT_QUOTES, 'UTF-8')), $division));
}

// als waarde leeg is, NULL wegschrijven in database (van "" naar "NULL", van "Jef" naar "'Jef'")
function toNull($input, $quote) {
    if ($input == "") {
        return "NULL";
    } else {
        if ($quote) {
            return "'" . $input . "'";
        } else {
            return $input;
        }
    }
}

// +----------------------------------------------------------
// | DATUM FUNCTIES
// +----------------------------------------------------------
// databasedatum in juiste formaat zetten (van yyyy-mm-dd naar dd/mm/jjjj)

function toDDMMYYYY($input) {
    if ($input == NULL) {
        return "";
    } else {
        return date("d/m/Y", strtotime($input));
    }
}

function toDDMM($input) {
    if ($input == NULL) {
        return "";
    } else {
        return date("d/m", strtotime($input));
    }
}

// ingegeven datum in formaat van database plaatsen (van dd/mm/jjjj naar yyyy-mm-dd)
function toYYYYMMDD($input) {
    if ($input == "") {
        return "";
    } else {
        $datum = explode("/", $input);
        return $datum[2] . "-" . $datum[1] . "-" . $datum[0];
    }
}

function unixToDDMMYYYYHIS($input) {
    if ($input == "") {
        return "";
    } else {
        return date("d/m/Y", $input) . " om " . date("H", $input) . "u" . date("i", $input);
    }
}

function unixToAgo($input) {
    $now = time();
    $then = $input;
    $diff = $now - $then;

    if ($input == "") {
        $return = "";
    } else {
        if ($diff < 10) {
            $return = "Zojuist";
        } elseif ($diff < 60) {
            $return = $diff . "sec geleden";
        } elseif ($diff < 3600) {
            $return = ceil($diff / 60) . "min geleden";
        } elseif ($diff < 86400) {
            $return = ceil($diff / 3600) . "u geleden";
        } else {
            $return = date("d/m/Y", $then);
        }
    }
    return $return;
}

// +----------------------------------------------------------
// | GETALLEN FUNCTIES
// +----------------------------------------------------------
// database decimaal getal tonen met komma (van 999.99 naar 999,99)
function toKomma($input) {
    if ($input == NULL) {
        return "0,00";
    } else {
        return number_format($input, 2, ',', '');
    }
}

// ingegeven decimaal getal omzetten in databaseformaat (van 999,99 naar 999.99)
function toPunt($input) {
    if ($input == "") {
        return "";
    } else {
        $getal = explode(",", $input);
        if (count($getal) == 2) {
            return $getal[0] . '.' . $getal[1];
        } else {
            return $getal[0];
        }
    }
}

// +----------------------------------------------------------
// | LANGUAGE FUNCTIES
// +----------------------------------------------------------

function ti($index, $makeLink = false) {
    $waarde = t($index, $makeLink, 1);
    return $waarde;
}

//function t($index, $makeLink = false, $important = null) {
//    $blTranslation = new BLTranslation();
//    $blTranslation_local = new BLTranslation_local();
//    $blLanguage = new BLLanguage();
//    $language = getLanguage();
//
//    $defaultLanguage = $blLanguage->getLanguageByPara("isDefault", 1);
//    if ($important == 1)
//        $translation = $blTranslation->getTranslationByPara("important = 1 AND wordIndex", $index, true);
//    else
//        $translation = $blTranslation->getTranslationByPara("important IS NULL AND wordIndex", $index, true);
//
//    if ($translation->getId() != null) {
//        $translation_local = $blTranslation_local->getTranslation_localByPara("languageId = " . $language->getId() . " and translationId", $translation->getId());
//        if ($translation_local->getId() == null) {
//            $transVal = $translation->getDefaultValue();
//        } else {
//            $transVal = $translation_local->getValue();
//        }
//    } else {
//        $transVal = ucfirst(str_replace("-", " ", $index));
//        $translation = new Translation(null, $index, $transVal, $important);
//        $id = $blTranslation->insertTranslation($translation);
//        $translation_local = new Translation_local(null, $id, $defaultLanguage->getId(), $transVal, makeLink($transVal));
//        $blTranslation_local->insertTranslation_local($translation_local);
//    }
//
//    if ($makeLink) {
//        $transVal = makeLink($transVal);
//    }
//
//    return $transVal;
//}



function templateImportant($index) {
    $waarde = template($index, 1);
    return $waarde;
}

function template($index, $important = null) {
    $blTemplate = new BLTemplate();
    $blTemplate_local = new BLTemplate_local();
    $blLanguage = new BLLanguage();
    $language = getLanguage();

    $defaultLanguage = $blLanguage->getLanguageByPara("isDefault", 1);
    if ($important == 1)
        $template = $blTemplate->getTemplateByPara("important = 1 AND wordIndex", $index, true);
    else
        $template = $blTemplate->getTemplateByPara("important IS NULL AND wordIndex", $index, true);

    if ($template->getId() != null) {
        $template_local = $blTemplate_local->getTemplate_localByPara("languageId = " . $language->getId() . " and templateId", $template->getId());
        if ($template_local->getId() == null) {
            $transVal = $template->getDefaultValue();
        } else {
            $transVal = $template_local->getValue();
        }
    } else {
        $transVal = ucfirst(str_replace("-", " ", $index));
        $template = new Template(null, $index, $transVal, $important);
        $id = $blTemplate->insertTemplate($template);
        $template_local = new Template_local(null, $id, $defaultLanguage->getId(), $transVal, null);
        $blTemplate_local->insertTemplate_local($template_local);
    }

    return $transVal;
}

function translate($word, $language) {
    $blLanguage = new BLLanguage();
    $blTranslation_local = new BLTranslation_local();

    $language = $blLanguage->getLanguageByPara("code", $language, true);

    $translation_locals = $blTranslation_local->getTranslation_localsByPara("languageId = " . curr_langId() . " and link", $word, true);

    if (count($translation_locals) == 1) {
        $translation_local = $translation_locals[0];
        $translation_local_language = $blTranslation_local->getTranslation_localByPara("languageId = " . $language->getId() . " and translationId", $translation_local->getTranslationId());
        return $translation_local_language->getValue();
    } else {
        return makeLink($word);
    }
}

function setLanguage($languageId) {
    $_SESSION["languageId"] = $languageId;
}

function getLanguage() {
    $blLanguage = new BLLanguage();
    $language = $blLanguage->getLanguageByPara("isDefault", 1);

    if (isset($_SESSION["languageId"]) && $blLanguage->isLanguage($_SESSION["languageId"])) {
        $language = $blLanguage->getLanguage($_SESSION["languageId"]);
    }
    return $language;
}

function curr_lang() {
    $blLanguage = new BLLanguage();
    return getLanguage()->getCode();
}

function curr_langId() {
    $blLanguage = new BLLanguage();
    $language = getLanguage();
    return $language->getId();
}

// +----------------------------------------------------------
// | DATABASE FUNCTIES
// +----------------------------------------------------------
function connect(&$connectie) {
    $connectie = mysqli_connect(HOST, USERNAME, PASSWORD);
    if (!$connectie) {
        die('Could not connect: ' . mysqli_error());
    }
    mysqli_select_db($connectie, DB_NAME);

    $sql = 'SET NAMES utf8';
    mysqli_query($connectie, $sql);
}

function dieObject($object) {
    echo "<pre>";
    print_r($object);
    echo "</pre>";
    die();
}

// +----------------------------------------------------------
// | IMAGE CACHE
// +----------------------------------------------------------
function image_cache($image, $map, $width, $height, $crop = false, $position = false) {
    global $image_cache;
    if ($image == "") {
        $path = $image_cache;
        $image = "default.png";
    } elseif ($image == "notfound") {
        $path = $image_cache;
        $image = "notfound.png";
    } else {
        $path = $image_cache . $map . "/";
    }

    if ($position == false) {
        $position = "center";
    }

    $cache_path = "images/cache/";

    $ext = explode(".", $image);
    $file_name = $ext[0];
    $ext = "." . $ext[1];

    if ($crop) $croptext = "-c"; else $croptext= "";
    
    //$cache_name = md5($file_name . "_" . $width . "_" . $height . "_" . $position) . $ext;
    
    $cache_name = $file_name . "-" . $width . "x" . $height . $croptext . "-" . $position . $ext;

    if (!file_exists($cache_path . $cache_name)) {

        
        copy($path . $image, $cache_path . $cache_name);
        if ($crop) {
            crop($cache_name, $width, $height, $position);
        } else {
            resize_img($cache_name, $width, $height);
        }
    }

    return "/" . $cache_path . $cache_name;
}

function crop($cache_name, $dest_width, $dest_height, $position) {

    $source_path = "images/cache/" . $cache_name;

// Add file validation code here

    list( $source_width, $source_height, $source_type ) = getimagesize($source_path);

    switch ($source_type) {
        case IMAGETYPE_GIF:
            $source_gdim = imagecreatefromgif($source_path);
            break;

        case IMAGETYPE_JPEG:
            $source_gdim = imagecreatefromjpeg($source_path);
            break;

        case IMAGETYPE_PNG:
            $source_gdim = imagecreatefrompng($source_path);
            break;
    }

    $source_aspect_ratio = $source_width / $source_height;
    $desired_aspect_ratio = $dest_width / $dest_height;

    if ($source_aspect_ratio > $desired_aspect_ratio) {
// Triggered when source image is wider
        if ($position == "center" || $position == "top" || $position == "bottom" || $position == "1" || $position == "4" || $position == "5") {
            $temp_height = $dest_height;
            $temp_width = (int) ($dest_height * $source_aspect_ratio );
        } elseif ($position == "left" || $position == "right" || $position == "2" || $position == "3") {
            $temp_height = $dest_height * 2;
            $temp_width = (int) ($dest_height * 2 * $source_aspect_ratio );
        }
    } else {
// Triggered otherwise (i.e. source image is similar or taller)
        if ($position == "center" || $position == "top" || $position == "bottom" || $position == "1" || $position == "4" || $position == "5") {
            $temp_width = $dest_width;
            $temp_height = (int) ( $dest_width / $source_aspect_ratio );
        } elseif ($position == "left" || $position == "right" || $position == "2" || $position == "3") {
            $temp_width = $dest_width * 2;
            $temp_height = (int) ( $dest_width * 2 / $source_aspect_ratio );
        }
    }

// Resize the image into a temporary GD image

    $temp_gdim = imagecreatetruecolor($temp_width, $temp_height);
    imagecopyresampled($temp_gdim, $source_gdim, 0, 0, 0, 0, $temp_width, $temp_height, $source_width, $source_height);

// Copy cropped region from temporary image into the desired GD image

    if ($position == "center" || $position == "1") {
        $x0 = ( $temp_width - $dest_width ) / 2;
        $y0 = ( $temp_height - $dest_height ) / 2;
    } elseif ($position == "left" || $position == "2") {
        $x0 = 0;
        $y0 = ($temp_height - $dest_height) / 2;
    } elseif ($position == "right" || $position == "3") {
        $x0 = $temp_width / 2;
        $y0 = ($temp_height - $dest_height) / 2;
    } elseif ($position == "top" || $position == "4") {
        $x0 = 0;
        $y0 = 0;
    } elseif ($position == "bottom" || $position == "5") {
        $x0 = 0;
        $y0 = $temp_height - $dest_height;
    }

    $desired_gdim = imagecreatetruecolor($dest_width, $dest_height);
    imagecopy($desired_gdim, $temp_gdim, 0, 0, $x0, $y0, $dest_width, $dest_height);

    imagejpeg($desired_gdim, $source_path);
}

function resize_img($cache_name, $dest_width, $dest_height) {
    $afbeelding = "images/cache/" . $cache_name;
    $image = new Imagick($afbeelding);

    if ($image->getImageWidth() < $dest_width && $image->getimageheight() < $dest_height) {
        $image->writeImage($afbeelding);
    } else {
        $image->scaleImage($dest_width, $dest_height, true);
        $image->writeImage($afbeelding);
    }
}

// +----------------------------------------------------------
// | SEARCH PARENT CATEGORIE
// +----------------------------------------------------------
function getParentCategorie($categorieId) {
    $blCategorie = new BLCategorie();
    $categorie = $blCategorie->getCategorie($categorieId);

    while ($categorie->getParentCategorieId() != null) {
        $categorie = $blCategorie->getCategorie($categorie->getParentCategorieId());
    }
    return $categorie->getId();
}

// +----------------------------------------------------------
// | FILTER
// +----------------------------------------------------------

function getParent($object, $objectId) {
    if($object == 21) {
        $blProduct = new BLProduct();
        $product = $blProduct->getProduct($objectId);
        $parentProduct = $product;
        if($product->getParentId() != null) {
            $parentProduct = $blProduct->getProduct($product->getParentId());
        }
        
        return $parentProduct;
        
    } else if($object == 2) {
        $blPagina = new BLPagina();
        $pagina = $blPagina->getPagina($objectId);
        $parentPagina = $pagina;
        if($pagina->getParentId() != null) {
            $parentPagina = $blPagina->getPagina($pagina->getParentId());
        }
        
        return $parentPagina;
        
    } else if($object == 22) {
        $blCategorie = new BLCategorie();
        $categorie = $blCategorie->getCategorie($objectId);
        $parentCategorie = $categorie;
        if($categorie->getParentId() != null) {
            $parentCategorie = $blCategorie->getCategorie($categorie->getParentId());
        }
        
        return $parentCategorie;
    }
}


function getProductenQuery($ACTIVE_FILTER) {
    $blProduct = new BLProduct();
    $blMerk = new BLMerk();
    $blCategorie = new BLCategorie();


    if (isset($ACTIVE_FILTER["naam"])) {

        $query = "(";

        $filternaam = toNull("%" . escape($ACTIVE_FILTER["naam"]) . "%", true);
        if (curr_lang() == "nl") {

            $query .= " (categoriegroup.categorieId in (
								SELECT t1.id AS lev1
								FROM categorie AS t1
								LEFT JOIN categorie AS t2 ON t2.parentCategorieId = t1.id
								WHERE t1.naam like $filternaam
								)
							or categoriegroup.categorieId in
							(
								SELECT t2.id AS lev2
								FROM categorie AS t1
								LEFT JOIN categorie AS t2 ON t2.parentCategorieId = t1.id
								LEFT JOIN categorie AS t3 ON t3.parentCategorieId = t2.id
								WHERE t1.naam like $filternaam 
							)
							or categoriegroup.categorieId in
							(
								SELECT t3.id AS lev3
								FROM categorie AS t1
								LEFT JOIN categorie AS t2 ON t2.parentCategorieId = t1.id
								LEFT JOIN categorie AS t3 ON t3.parentCategorieId = t2.id
								LEFT JOIN categorie AS t4 ON t4.parentCategorieId = t3.id
								WHERE t1.naam like $filternaam
							)
							or categoriegroup.categorieId in
							(
								SELECT t4.id AS lev4
								FROM categorie AS t1
								LEFT JOIN categorie AS t2 ON t2.parentCategorieId = t1.id
								LEFT JOIN categorie AS t3 ON t3.parentCategorieId = t2.id
								LEFT JOIN categorie AS t4 ON t4.parentCategorieId = t3.id
								LEFT JOIN categorie AS t5 ON t5.parentCategorieId = t4.id
								WHERE t1.naam like $filternaam
							))";
            $query .= " or ";
            $query .= " product.naam like $filternaam or product.code like $filternaam)";
            $query .= " and product.languageId = " . curr_langId();
        } else {
            $categorieen = $blCategorie->getCategoriesByFilter(" where categorie.naam like " . $filternaam . " and languageId = " . curr_langId());

//            dieObject($categorieen);

            for ($i = 0; $i < count($categorieen); $i++) {
                $parentId = $categorieen[$i]->getParentId();

                if ($i != 0) {
                    $query .= " or ";
                }

                $query .= " (categoriegroup.categorieId in (
                                    SELECT t1.id AS lev1
                                    FROM categorie AS t1
                                    LEFT JOIN categorie AS t2 ON t2.parentCategorieId = t1.id
                                    WHERE t1.id = $parentId
                            )
                            or categoriegroup.categorieId in
                            (
                                    SELECT t2.id AS lev2
                                    FROM categorie AS t1
                                    LEFT JOIN categorie AS t2 ON t2.parentCategorieId = t1.id
                                    LEFT JOIN categorie AS t3 ON t3.parentCategorieId = t2.id
                                    WHERE t1.id = $parentId 
                            )
                            or categoriegroup.categorieId in
                            (
                                    SELECT t3.id AS lev3
                                    FROM categorie AS t1
                                    LEFT JOIN categorie AS t2 ON t2.parentCategorieId = t1.id
                                    LEFT JOIN categorie AS t3 ON t3.parentCategorieId = t2.id
                                    LEFT JOIN categorie AS t4 ON t4.parentCategorieId = t3.id
                                    WHERE t1.id = $parentId
                            )
                            or categoriegroup.categorieId in
                            (
                                    SELECT t4.id AS lev4
                                    FROM categorie AS t1
                                    LEFT JOIN categorie AS t2 ON t2.parentCategorieId = t1.id
                                    LEFT JOIN categorie AS t3 ON t3.parentCategorieId = t2.id
                                    LEFT JOIN categorie AS t4 ON t4.parentCategorieId = t3.id
                                    LEFT JOIN categorie AS t5 ON t5.parentCategorieId = t4.id
                                    WHERE t1.id = $parentId
                            )) ";
                if ($i == count($categorieen) - 1) {
                    $query .= " or";
                }
            }
            $query .= " product.naam like $filternaam or product.code like $filternaam)";
            $query .= " and prod.languageId = " . curr_langId();
        }



        if ($query != "") {
            $query = " where " . $query;
        } else {
            $query = " where";
        }
    }

    return $query;
}


function tekstInkorten($string, $lengte=20,$stripTags=false){
    if ($stripTags) $string = strip_tags ($string);
    $woorden = explode(" ",$string);
    if (count($woorden)>$lengte) 
        {
        $woorden = array_slice($woorden,0,$lengte);
        if ($closePTag) $woorden[] = "...</p>"; else $woorden[] = "...";
        }

    $string = implode(" ",$woorden);
    return $string;
}

?>