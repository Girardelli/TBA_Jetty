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
			<td class="bodytekst" valign="top" bgcolor="FFFFFF"><br><br><span
				class="bodytitle">Help pagina</span> <br> <br> <span
				class="bodysubtitle">Algemeen:</span> <br>
				<table border="0" cellspacing="4" cellpadding="4">
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytekst">De informatie op de aangeboden webpagina is direct gekoppeld aan onze oproependatabase. Elke oproep die wij voor u behandelen wordt automatisch in deze database bewaard.<br>
                  Onze medewerkers voegen extra informatie toe om u zoveel mogelijk courante informatie te verschaffen. U beschikt dus steeds over de allerlaatste informatie. <br>
                  Om uw informatie te beschermen zal uw aanmeldsessie verlopen als u de webpagina enige tijd niet gebruikt. Deze tijdslimiet staat voor u ingesteld op
                  <%=Constants.CUSTOMER_SESSION_TIMEOUT / 60000%> minuten.</td>
					</tr>
				</table> <br> <span class="bodysubtitle">Symbolen:</span>
				<table border="0" cellspacing="4" cellpadding="4">
                    <tr>
                        <td width="25"></td>
                        <td valign="top" class="bodytekst">De volgende informatiesymbolen worden gebruikt op de oproepenpagina.
                        </td>
                    </tr>
                 </table>
                 <table border="0" cellspacing="4" cellpadding="4">
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodysubsubtitle"><img
							src="/tba/images/incall.gif" height="13">
						</td>
						<td valign="top" class="bodytekst">een inkomende oproep.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodysubsubtitle"><img
							src="/tba/images/outcall.gif" height="13">
						</td>
						<td valign="top" class="bodytekst">een uitgaande oproep.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodysubsubtitle"><img
							src="/tba/images/info.gif" height="16">
						</td>
						<td valign="top" class="bodytekst">tekstinformatie voor een oproep kan uit 2 delen bestaan: een korte omschrijving die steeds ingevuld en zichtbaar is in de oproepenlijst. 
                  Indien noodzakelijk kan men gebruik maken van een langere omschrijving. Indien deze laatste beschikbaar is, wordt dit aangegeven door dit symbool. 
                  Door dubbel te klikken op de oproep in uw oproepenlijst kan u deze extra informatie bekijken.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodysubsubtitle"><img
							src="/tba/images/agenda.gif" height="13">
						</td>
						<td valign="top" class="bodytekst">deze oproep resulteerde tot een afspraak in uw agenda.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodysubsubtitle"><img
							src="/tba/images/telefoon.gif" height="13">
						</td>
						<td valign="top" class="bodytekst">telefonisch contact of doorschakeling.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodysubsubtitle"><img
							src="/tba/images/sms.gif" height="13">
						</td>
						<td valign="top" class="bodytekst">indien u niet bereikbaar bent voor een dringende oproep, sturen wij u een SMS bericht naar uw mobiel.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodysubsubtitle"><img
							src="/tba/images/important.gif" height="13">
						</td>
						<td valign="top" class="bodytekst">Deze oproep vraagt opvolging of de nodige aandacht van u. Indien onze medewerkers dit aanduiden, zal er onmiddellijk een e-mail naar u verstuurd worden om u op de hoogte te brengen van deze dringende oproep.<br>
                    Indien u ingelogd bent in ons portaal zal er ook een notificatie op uw scherm verschijnen om aan te geven dat er een dringende oproep is.
                        </td>
					</tr>
				</table> 
                <br> <span class="bodysubtitle">Oproepen opvolgen:</span> <br>
				<table border="0" cellspacing="4" cellpadding="4">
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytekst">Door met de linkermuisknop dubbel te klikken op een oproep in de oproepenlijst, worden alle gegevens van deze oproep in een nieuw scherm getoond. 
                  Hier heeft u de mogelijkheid om instructies aan deze oproep toe te voegen in het veld 'opvolging'. 
                  Deze informatie wordt bij de omschrijvingstekst gezet in een andere kleur.<br> 
                    U kan dus deze functie gebruiken voor het opvolgen, te nemen of genomen acties, enz. Deze informatie kan later nuttig zijn als u wil nakijken wat er gebeurd is naar aanleiding van een oproep. <br>
						</td>
					</tr>
                </table> 
                <br> <span class="bodysubtitle">Oproepen archiveren:</span> <br>
                <table border="0" cellspacing="4" cellpadding="4">
                    <tr>
                        <td width="25"></td>
                        <td valign="top" class="bodytekst">U kan in de oproepenlijst oproepen selecteren door op de lijn te klikken: de oproep zal van kleur veranderen. Er kunnen zo meerdere oproepen geselecteerd worden.<br>
                  Door op de 'Archiveer' knop te drukken worden deze oproepen gearchiveerd. Ze zullen niet langer in uw oproepenlijst opgenomen worden. Je kan deze nog wel raadplegen via het menu 'Gearchiveerde oproepen'.<br>
                  De zoekfunctie zal ook de gearchiveerde oproepen doorzoeken.<br>
                  Gearchiveerde oproepen worden in het blauw weergegeven.
                        </td>
                    </tr>
				</table> 
                <br> <span class="bodysubtitle">Opdrachten doorgeven:</span> <br>
                <table border="0" cellspacing="4" cellpadding="4">
                    <tr>
                        <td width="25"></td>
                        <td valign="top" class="bodytekst">Met de functie kan je administratieve opdrachten definiëren en te verwerken bestanden opladen naar ons kantoor.<br>
                  U kan de opdracht omschrijven, een gewenste opleverdatum opgeven en bestanden opladen die bij deze opdracht horen (bv geluidsbestanden die moeten uitgeschreven worden).<br>
                  Onze medewerkers zullen automatisch geinformeerd worden als er een nieuw opdracht is ingevoerd. Je kan de status van verwerking volgen en u zal een e-mailbericht ontvangen als de opdracht is voltooid.<br>
                  De resultaten van de opdracht kunnen van deze pagina worden opgeladen.
                       </td>
                    </tr>
                </table> 
                <br> <span class="bodysubtitle">Oproepen zoeken:</span> <br>
                <table border="0" cellspacing="4" cellpadding="4">
                    <tr>
                        <td width="25"></td>
                        <td valign="top" class="bodytekst">Met de zoekfunctie kan u oproepen opsporen op basis van een zoektekst. Alle oproepen (beschrijving, tijd, nummer, naam, enz) worden doorzocht op het bevatten van de opgegeven zoektekst. 
                    De gevonden oproepen worden weergegeven met de zoektekst in het vet en worden onderlijnd.<br>
                    U kan dus delen van telefoonnummers, data, namen, enzomeer ingeven om tot het zoekresultaat te komen.
                       </td>
                    </tr>
                </table> 
                <br> <span class="bodysubtitle">Persoonlijke instellingen</span> <br>
				<table border="0" cellspacing="4" cellpadding="4">
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytekst">Dit scherm laat u toe uw oproepenbehandeling te personaliseren. De volgende opties zijn beschikbaar.<br></td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodysubsubtitle">E-mail adres(sen)</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytekst">Hier kan u 1 of meerdere e-mailadressen specifiëren. De automatische oproepen mailing zal verstuurd worden naar alle adressen in de lijst. 
                  Indien meer dan 1 adres wordt ingegeven, dan moet elk adres gescheiden zijn door een ';' (punt-komma) teken.<br>
                        </td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodysubsubtitle">GSM nummer</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytekst">Vul hier uw mobiel nummer
							in. Dit zullen wij gebruiken voor het versturen van SMS
							berichten.<br></td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodysubsubtitle">Automatische mailing</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytekst">Met deze selectie kan u tot max. 3x/dag een e-mail te ontvangen met daarin de oproepgegevens sinds de vorige mail.<br></td>
					</tr>
                    <tr>
                        <td width="25"></td>
                        <td valign="top" class="bodysubsubtitle">Zend geen lege mails</td>
                    </tr>
                    <tr>
                        <td width="25"></td>
                        <td valign="top" class="bodytekst">Met deze selectie kan u ervoor zorgen dat er geen oproepen verstuurd worden als er sinds de vorige mail geen nieuwe oproepen werden verwerkt.<br></td>
                    </tr>
                    <tr>
                        <td width="25"></td>
                        <td valign="top" class="bodysubsubtitle">Zend geen tekst formaat</td>
                    </tr>
                    <tr>
                        <td width="25"></td>
                        <td valign="top" class="bodytekst">Selecteer deze optie als u verkiest om de oproepenmail niet in 'html' maar in 'text' formaat te ontvangen.<br></td>
                    </tr>
				</table></td>
		</tr>

	</table>
   <br><br><br>

</body>

</html>

