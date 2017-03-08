$(document).ready(function () {

    $(".fancybox").fancybox({
        maxWidth: 800,
        maxHeight: 600,
        fitToView: true,
        width: '90%',
        height: '90%',
        autoSize: false,
        closeClick: false,
        openEffect: 'none',
        closeEffect: 'none'
    });
    $(".various").fancybox({
        maxWidth: 800,
        maxHeight: 600,
        fitToView: false,
        width: '70%',
        height: '70%',
        autoSize: false,
        closeClick: false,
        openEffect: 'none',
        closeEffect: 'none'
    });
    //LANGUAGE MENU
    $('.lang-toggle').click(function () {
        $('.lang-menu').slideToggle();
    });





    $('.legalCookieAccept').click(function (e) {
        e.preventDefault();
        $.ajax({
            url: '/execute.php?page=legalCookie',
            type: "POST",
            dataType: 'json',
            data: {
                ok: 'ok'
            },
            success: function (data) {
                $(".cookie-banner").hide();
            },
            error: function (data) {
                console.log("fout....");
            }
        });
    });

    /*====================================================
     MODAL
     =====================================================*/
    $(".modalPop").click(function (evt) {
        evt.preventDefault();
        var dezelink = $(this).attr("href");
        $('#modalLoad').modal({
            backdrop: 'static',
            keyboard: false,
            show: true
        });
        setTimeout(function () {
            location.href = dezelink;
        }, 500);
    });

    /*====================================================
     FAQ
     =====================================================*/
    $('.showAnswer').click(function (e) {
        e.preventDefault();
        var id = $(this).attr('rel');
        $('#antwoord' + id).slideToggle();
        $(this).children('i').toggleClass('fa-chevron-down fa-chevron-up');
    });

    $(".feedbackJA").click(function (evt) {
        evt.preventDefault();
        var vraagId = $(this).attr("rel");
        $.ajax({
            url: '/execute.php?page=ajax_feedback',
            type: "POST",
            dataType: 'json',
            data: {
                vraagId: vraagId,
                feedback: "ja"
            },
            success: function (data) {
                if (data.response == true) {
                    $('#modalFeedbackJA').modal({
                        show: true
                    });
                } else {
                    alert(data.error);
                }
            }
        });
        $("#feedback" + vraagId).hide();
    });
    $(".feedbackNEE").click(function (evt) {
        evt.preventDefault();
        var vraagId = $(this).attr("rel");
        $.ajax({
            url: '/execute.php?page=ajax_feedback',
            type: "POST",
            dataType: 'json',
            data: {
                vraagId: vraagId,
                feedback: "nee"
            },
            success: function (data) {
                if (data.response == true) {
                    $('#modalFeedbackNEE').modal({
                        show: true
                    });
                } else {
                    alert(data.error);
                }
            }
        });
        $("#feedback" + vraagId).hide();
    });




    /*====================================================
     SOCIAL SHARE
     =====================================================*/
    $(".delenFB").click(shareFB);
    $(".delenTwitter").click(shareTwitter);
    $(".delenLinkedin").click(shareLinkedin);
    $(".delenGooglePlus").click(shareGooglePlus);
    $(".delenPinterest").click(sharePinterest);
    function shareGooglePlus(evt) {
        evt.preventDefault();
        var link = $(this).attr("share-link");
        window.open("https://plus.google.com/share?url=" + link, 'mywindow', 'width=590,height=665');
    }
    function shareLinkedin(evt) {
        evt.preventDefault();
        var link = $(this).attr("share-link");
        window.open('https://www.linkedin.com/cws/share?url=' + link + '&token=&isFramed=true&lang=en_US&_ts=1415799897533.8362', 'mywindow', 'width=610,height=500');
    }
    function shareFB(evt) {
        evt.preventDefault();
        var link = $(this).attr("share-link");
        window.open('http://www.facebook.com/sharer/sharer.php?u=' + link, 'mywindow', 'width=650,height=330');
    }
    function shareTwitter(evt) {
        evt.preventDefault();
        var link = $(this).attr("share-link");
        var titel = $(this).attr("share-titel");
        window.open('https://twitter.com/intent/tweet?button_hashtag=RGBLed&original_referer=' + link + '&source=tweetbutton&text=' + titel + '&url=' + link, 'mywindow', 'width=700,height=520');
    }
    function sharePinterest(evt) {
        evt.preventDefault();
        var link = $(this).attr("share-link");
        var titel = $(this).attr("share-titel");
        var image = $(this).attr("share-media");
        window.open('http://pinterest.com/pin/create/link/?url=' + link + '&media=' + image + '&description=' + titel, 'mywindow', 'width=700,height=520');
    }

    /*====================================================
     HAMBURGER
     =====================================================*/
    $('.responsive-button').click(function (e) {
        $(this).toggleClass('open');
        $('body').toggleClass('open');
        $('.mobile-menu').slideToggle();
    });
    /*====================================================
     Mobile sub menu
     =====================================================*/
    $('.toggleSub').click(function (e) {
        e.preventDefault();

        $(this).toggleClass("open");
        $(this).next("ul").slideToggle();

    });
//    $('.toggleSub').click(function (e) {
//        e.preventDefault();
//        $(".toggleSub").removeClass("open");
//        $(".submenu").each(function () {
//            $(this).slideUp();
//        });
//
//        if (!$(this).hasClass('open')) {
//            $(this).addClass('open');
//        } else {
//            $(".toggleSub").removeClass("open");
//        }
//
//        if (!$(this).parent().find(".submenu").is(":visible")) {
//            $(this).parent().find(".submenu").slideDown();
//        }
//    });
//    
//    
//
//    // Close dropdowns on click
//    var ua = navigator.userAgent,
//            event = (ua.match(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i)) ? "touchstart" : "click";
//    $(document).on(event, function () {
//        if ($(".submenu").is(":visible")) {
//            $(".submenu").slideUp();
//        }
//        if ($(".toggleSub").hasClass("open")) {
//            $(".toggleSub").removeClass("open");
//        }
//    });
//
//    // Prevent close dropdown on click <li>
//    $(".submenu").on(event, function (e) {
//        e.stopPropagation();
//    });
//
//    $('.toggleSub').click(function (evt) {
//        evt.preventDefault();
//        evt.stopPropagation();
//        $('.submenu', this).slideToggle();
//    });


    /*====================================================
     CHECKBOX IN DROPDOWN
     =====================================================*/
// Close dropdowns on click
    if ($(window).width() > 768) {
        var ua = navigator.userAgent,
                event = (ua.match(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i)) ? "touchstart" : "click";
        $(document).on(event, function () {
            $(".dropdown-wrapper-multiple .dropdown").slideUp();
            $(".dropdown-wrapper .dropdown").slideUp();
            $(".select").removeClass("open");
            if ($(".subMenu").is(":visible")) {
                $(".subMenu").slideUp();
            }
        });
    }

// Prevent close dropdown on click <li>
    $(".dropdown-wrapper-multiple .dropdown, .dropdown-wrapper-multiple .select").on(event, function (e) {
        e.stopPropagation();
    });

    $(".select").on("click", function (e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).toggleClass("open");
        $(this).siblings(".dropdown").slideToggle("fast");
        $(".dropdown").not($(this).siblings(".dropdown")).slideUp("fast");
        // andere sluiten
    });

// Waarde voor input elke keer uit select halen -> niet alleen als veranderd wordt
    $(".select").each(function () {
        var behandelingenString = "Selecteer 1 of meerdere diensten";
        var naam = "";
        $(this).siblings('.dropdown').find("li").each(function () {
            if ($(this).find("input[type=checkbox]").is(":checked")) {
                naam = $(this).find("label").text();
                behandelingenString = naam;
            }
            $(this).parent().siblings(".select").find(".value").text(behandelingenString);
        });
        $(this).siblings("input").val($(this).find(".value").text().replace(".", "").replace(".", ""));
    });

// Change van checkbox 
    $(".dropdown-wrapper-multiple").on("change", "input[type=checkbox]", function (e) {
        var counter = 0;
        var categorieNaam = "";
        var naam = "";
        var functionData;
        // Gemeentes dropdown -> with SEARCH (set SESSION to see if checked for dynamically created list)
        if ($(this).attr("name") === "gemeentes[]") {
            var checked = "unchecked";
            if ($(this).is(":checked")) {
                checked = "checked";
            }
            var postcode = $(this).val();
            var gemeente = $(this).attr("data-gemeente");
        }
        // Types dropdown -> NO SEARCH
        else {
            var behandelingenString = "";
            var komma = "";
            $(this).parent().parent().parent().find("li").each(function () {
                if ($(this).find("input[type=checkbox]").is(":checked")) {
                    naam = $(this).find("label").text();
                    counter += 1;
                    if (counter > 1) {
                        komma = ", ";
                    }
                    behandelingenString = behandelingenString + komma + naam;
                }
            });
            if (counter === 0) {
                if ($(this).parent().parent().siblings(".select").find(".value").attr('id') === 'diensten') {
                    $(this).parent().parent().siblings(".select").find(".value").text("Selecteer 1 of meerdere diensten");
                }
            }
            if (counter > 0 && counter < 3) {
                $(this).parent().parent().siblings(".select").find(".value").text(behandelingenString);
            }
            if (counter >= 3) {
                if ($(this).parent().parent().siblings(".select").find(".value").attr('id') === 'diensten') {
                    $(this).parent().parent().siblings(".select").find(".value").text(counter + " diensten");
                }
            }
        }
    });

    $('.banner-slider').slick({
        slidesToShow: 1,
        slidesToScroll: 1,
        arrows: false,
        dots: true,
        fade: false,
        autoplay: true,
        pauseOnHover: false,
        autoplaySpeed: 5000
    });
});