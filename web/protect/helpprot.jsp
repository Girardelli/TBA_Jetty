<html>
<%@ include file="protheader.jsp" %>

<%
vSession.setCallingJsp(Constants.CLIENT_CALLS_JSP);
%>
<head>
</head>
	<table  cellspacing='0' cellpadding='0' border='0'
		bgcolor="FFFFFF">

		<!--Update account jsp-->

		<tr>
			<td valign="top" width="30" bgcolor="FFFFFF"></td>
			<td class="bodytext" valign="top" bgcolor="FFFFFF"><br><br><span
				class="admintitle">Help pagina's</span> <br> <br> <span
				class="admintitle">Algemeen:</span> <br>
				<table border="0" cellspacing="4" cellpadding="4">
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytext">De informatie op de aangeboden webpagina is direct gekoppeld aan onze oproependatabase. 
                  Elke oproep die wij voor u behandelen wordt automatisch in deze database bewaard. Onze medewerkers voegen extra informatie toe 
                  om u zoveel mogelijk courante informatie te verschaffen. U beschikt dus steeds over de allerlaatste informatie. <br>Om uw informatie 
                  te beschermen zal uw aanmeldsessie verlopen als u de webpagina enige tijd niet gebruikt. Deze tijdslimiet staat voor u ingesteld 
                  <%=Constants.CUSTOMER_SESSION_TIMEOUT / 100000%> minuten.</td>
					</tr>
				</table> <br> <span class="admintitle">Symbolen:</span>
				<table border="0" cellspacing="4" cellpadding="4">
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle"><img
							src="/tba/images/incall.gif" height="13">
						</td>
						<td valign="top" class="bodytext">een
							binnenkomende oproep.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle"><img
							src="/tba/images/outcall.gif" height="13">
						</td>
						<td valign="top" class="bodytext">een
							uitgaande oproep.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle"><img
							src="/tba/images/info.gif" height="16">
						</td>
						<td valign="top" class="bodytext">tekstinformatie voor een oproep kan uit 2 delen bestaan: een korte omschrijving welke steeds 
                  is ingevuld en zichtbaar is in de oproepenlijst. Indien noodzakelijk kan men gebruik maken van een lange omschrijving. Indien deze 
                  laatste beschikbaar is, wordt dit aangegeven door dit symbool. Door dubbel te klikken op de oproep in uw oproepenlijst kan u deze 
                  extra informatie bekijken.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle"><img
							src="/tba/images/agenda.gif" height="13">
						</td>
						<td valign="top" class="bodytext">afspraak.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle"><img
							src="/tba/images/telefoon.gif" height="13">
						</td>
						<td valign="top" class="bodytext">telefonisch contact of doorschakeling.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle"><img
							src="/tba/images/sms.gif" height="13">
						</td>
						<td valign="top" class="bodytext">indien u niet bereikbaar bent voor een dringende oproep, sturen wij u een SMS bericht naar uw mobiel.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle"><img
							src="/tba/images/important.gif" height="13">
						</td>
						<td valign="top" class="bodytext">Urgentie / nodige aandacht.</td>
					</tr>
				</table> <br> <span class="admintitle">Oproepen opvolgen:</span> <br>
				<table border="0" cellspacing="4" cellpadding="4">
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytext">Door met de linkermuisknop dubbel te klikken op een oproep in de oproepenlijst, worden alle gegevens 
                  van deze oproep in een nieuw scherm getoond. Hier heeft u de mogelijkheid om uw eigen commentaar aan deze oproep toe te voegen in het veld 'opvolging'. 
                  Deze informatie wordt bij de omschrijvingstekst gezet in een andere kleur. <br> 
                  U kan dus deze functie gebruiken voor het opvolgen, te nemen of genomen acties, enz. Deze informatie kan later nuttig zijn als u wil nakijken wat er 
                  gebeurd is naar aanleiding van een oproep. <br>
						</td>
					</tr>
                </table> <br> <span class="admintitle">Oproepen archiveren:</span> <br>
                <table border="0" cellspacing="4" cellpadding="4">
                    <tr>
                        <td width="25"></td>
                        <td valign="top" class="bodytext">U kan in de oproepenlijst oproepen selecteren door op de lijn te klikken: de oproep zal van kleur veranderen. 
                        Er kunnen zo meerdere oproepen geselecteerd worden.<br>
                        Door op de 'Archiveer' knop te drukken worden deze oproepen gearchiveerd. Ze zullen niet langer in uw oproepenlijst opgenomen worden. Je kan 
                        deze nog wel raadplegen via het menu 'Gearchiveerde oproepen'.<br>
                        De zoekfunctie zal ook de gearchiveerde oproepen doorzoeken.<br>
                        </td>
                    </tr>
				</table> <br> <span class="admintitle">Oproepen zoeken:</span> <br>
				<table border="0" cellspacing="4" cellpadding="4">
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytext">Met de zoekfunctie kan u oproepen opsporen op basis van een zoektekst. Alle oproepen (beschrijving, tijd, nummer, naam, enz) 
                  worden doorzocht op het bevatten van de opgegeven zoektekst. De gevonden oproepen worden weergegeven met de zoektekst in het vet en worden onderlijnd.<br> 
                  U kan dus delen van telefoonnummers, data, namen, enzomeer ingeven om tot het gewenst zoekresultaat te komen.<br>
                       </td>
					</tr>
				</table> <br> <span class="admintitle">Persoonlijke instellingen</span> <br>
				<table border="0" cellspacing="4" cellpadding="4">
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytext">Dit scherm laat u toe uw oproepenbehandeling te personaliseren. De volgende opties zijn beschikbaar.<br></td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle">E-mail adres(sen)</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytext">Hier kan u 1 of meerdere e-mailadressen specifieren. De automatische oproepen mailing zal verstuurd worden naar alle adressen in de lijst. 
                  Indien meer dan 1 adres wordt ingegeven, dan moet elk adres gescheiden zijn door een ';' (punt-komma) teken.<br></td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle">GSM nummer</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytext">Vul hier uw mobiel nummer
							in. Dit zullen wij gebruiken voor het versturen van SMS
							berichten.<br></td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle">Automatische mailing</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytext">Met deze selecties kan u instellen om tot maximaal 3 maal daags een e-mail te ontvangen met daarin de oproepgegevens sinds de vorige mail.<br></td>
					</tr>
                    <tr>
                        <td width="25"></td>
                        <td valign="top" class="adminsubsubtitle">Zend geen lege mails</td>
                    </tr>
                    <tr>
                        <td width="25"></td>
                        <td valign="top" class="bodytext">Met deze selectie kan u ervoor zorgen dat er geen oproepen verstuurd worden als er sinds de vorige mail geen nieuwe oproepen werden verwerkt.<br></td>
                    </tr>
                    <tr>
                        <td width="25"></td>
                        <td valign="top" class="adminsubsubtitle">Zend geen tekst formaat</td>
                    </tr>
                    <tr>
                        <td width="25"></td>
                        <td valign="top" class="bodytext">Selecteer deze optie als u verkiest om de oproepenmail niet in 'html' maar in 'text' formaat te ontvangen.<br></td>
                    </tr>
				</table></td>
		</tr>

	</table>

</body>

</html>

