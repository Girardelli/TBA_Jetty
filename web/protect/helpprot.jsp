<html>
<%@ include file="protheader.jsp" %>

<head>
</head>
	<table width='100%' cellspacing='0' cellpadding='0' border='0'
		bgcolor="FFFFFF">

		<!--Update account jsp-->

		<tr>
			<td valign="top" width="30" bgcolor="FFFFFF"></td>
			<td class="bodytext" valign="top" bgcolor="FFFFFF"><br><br><span
				class="admintitle">Help pagina's</span> <br> <br> <span
				class="adminsubtitle">Algemeen:</span> <br>
				<table width="700" border="0" cellspacing="4" cellpadding="4">
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytext">De informatie aangeboden op
							deze pagina's is direct gekoppeld aan onze oproependatabase. Elke
							oproep die wij binnenkrijgen wordt automatisch in deze database
							bewaard. Onze medewerkers voegen extra informatie toe om u zoveel
							mogelijk informatie voor elke oproep te verschaffen.<br> U
							beschikt dus steeds over de allerlaatste informatie. Bovendien
							wordt deze oproepenlijst automatisch herladen elke <%=Constants.REFRESH%>
							seconden.<br> Om uw informatie te beschermen zal uw
							aanmeldsessie verlopen als deze pagina's een tijdje niet gebruikt
							worden. Deze tijdslimiet staat ingesteld op <%=Constants.CUSTOMER_SESSION_TIMEOUT / 100000%>
							minuten.</td>
					</tr>
				</table> <br> <span class="adminsubtitle">Symbolen:</span>
				<table width="700" border="0" cellspacing="4" cellpadding="4">
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle"><img
							src="/TheBusinessAssistant/images/incall.gif" height="13">
						</td>
						<td valign="top" class="bodytext">wordt gebruikt om een
							binnenkomende oproep aan te geven.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle"><img
							src="/TheBusinessAssistant/images/outcall.gif" height="13">
						</td>
						<td valign="top" class="bodytext">wordt gebruikt om een
							uitgaande oproep aan te geven.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle"><img
							src="/TheBusinessAssistant/images/info.gif" height="16">
						</td>
						<td valign="top" class="bodytext">tekstinformatie voor een
							oproep kan uit 2 delen bestaan: een korte omschrijving welke
							steeds is ingevuld en zichtbaar is in de oproepenlijst. En
							mogelijk een lange omschrijving. Indien deze laatste beschikbaar
							is wordt dit aangegeven door dit symbool. Door dubbel te klikken
							op de oproep in uw oproepenlijst kan u deze extra informatie
							bekijken.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle"><img
							src="/TheBusinessAssistant/images/agenda.gif" height="13">
						</td>
						<td valign="top" class="bodytext">geeft aan dat een oproep
							resulteerde in een nieuwe afspraak in uw agenda of een wijziging
							van een bestaande afspraak.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle"><img
							src="/TheBusinessAssistant/images/telefoon.gif" height="13">
						</td>
						<td valign="top" class="bodytext">voor dringende oproepen
							proberen wij u telefonisch te bereiken voor informatie of om de
							oproep eventueel door te geven.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle"><img
							src="/TheBusinessAssistant/images/sms.gif" height="13">
						</td>
						<td valign="top" class="bodytext">indien u niet bereikbaar
							bent voor een dringende oproep, sturen wij u een SMS bericht naar
							uw GSM. Dit wordt aangegeven door dit symbool.</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle"><img
							src="/TheBusinessAssistant/images/important.gif" height="13">
						</td>
						<td valign="top" class="bodytext">met dit symbool proberen we
							uw aandacht te vestigen naar een oproep die dringende actie
							vereist.</td>
					</tr>
				</table> <br> <span class="adminsubtitle">Oproepen opvolgen:</span> <br>
				<table width="700" border="0" cellspacing="4" cellpadding="4">
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytext">Door met de linkermuisknop
							dubbel te klikken op een oproep in de oproepenlijst, worden alle
							gegevens van deze oproep in een nieuw scherm getoond. Hier heeft
							u de mogelijkheid om uw eigen commentaar aan deze oproep toe te
							voegen in het veld 'opvolging'. Deze informatie wordt bij de
							omschrijvingstekst gezet in een andere kleur. <br> U kan dus
							deze functie gebruiken voor het opvolgen, bv te nemen of genomen
							acties, enz. Deze informatie kan later nuttig zijn als u wil
							nakijken wat er gebeurd is naar aanleiding van een oproep. <br>
						</td>
					</tr>
				</table> <br> <span class="adminsubtitle">Oproepen zoeken:</span> <br>
				<table width="700" border="0" cellspacing="4" cellpadding="4">
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytext">Met de zoekfunctie kan u
							oproepen opsporen op basis van een zoektekst. Alle
							oproepinformatie (beschrijving, tijd, nummer, naam, enz) wordt
							doorzocht op het bevatten van de opgegeven zoektekst. De gevonden
							oproepen worden weergegeven met de zoektekst in het vet en worden
							onderlijnd.<br> U kan dus delen van telefoonnummers, data,
							namen, enzomeer ingeven om tot het gewenst zoekresultaat te
							komen.<br></td>
					</tr>
				</table> <br> <span class="adminsubtitle">Persoonlijke
					instellingen</span> <br>
				<table width="700" border="0" cellspacing="4" cellpadding="4">
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytext">Dit scherm laat u toe uw
							oproepenbehandeling te peronaliseren. De volgende opties zijn
							beschikbaar.<br></td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="adminsubsubtitle">E-mail adres(sen)</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytext">Hier kan u 1 of meerdere
							e-mailadressen specifieren. De automatische oproepen mailing zal
							verstuurd worden naar alle adressen in de lijst. Indien meer dan
							1 adres wordt ingegeven, dan moet elk adres gescheiden zijn door
							een ';' (punt-komma) teken.<br></td>
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
						<td valign="top" class="adminsubsubtitle">Automatische
							mailing</td>
					</tr>
					<tr>
						<td width="25"></td>
						<td valign="top" class="bodytext">Met deze selecties kan u
							instellen om tot maximaal 3 maal daags een e-mail te ontvangen
							met daarin de oproepgegevens sinds de vorige mail.<br></td>
					</tr>
				</table> <br></td>
		</tr>

	</table>

</body>

</html>

