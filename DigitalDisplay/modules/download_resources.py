from bs4 import BeautifulSoup
import json
import logging
import email
import imaplib
import string
import datetime

LOGGER = logging.getLogger('wso2server.remote_data')


class SalesRemoteData():

    br = None
    cj = None
    json_tables={}
    flag = 0

    dashboard_url="https://na9.salesforce.com/01ZE0000000kI99"

    img1="https://na9.salesforce.com/servlet/servlet.ChartServer?rsid=0FLE0000001MLKC&ruid=005E00000071TMb&did=01ZE0000000kI99&s=8&fs=12&tfg=12&tfs=-3407872&explode=0&c=bar&cs=0&title=&eh=no&compo=no&fg=-16777216&bg1=-1&bg2=-1&bgdir=0&dl1=Fiscal+Period&dl2=Account+Name&l=2&sax=no&Yman=no&nc=0&actUrl=%2F00OE000000366Bl%3Fdbw%3D1&sd=1&scv=yes&sct=no&spt=no&bd=yes&cu=USD&ab=X&u=0&vt=0&ab2=Y&u2=1&vt2=3&vl0=Best+Case&vl1=Expected+Case&vl2=Worst+Case&spoc=no&topn=no&gm=0.0&gc0=-1&gm0=1.0&gc1=-1&gm1=2.0&gc2=-1&gm2=3.0&sona=0&refreshts=1446054620000"
    img2="https://na9.salesforce.com/servlet/servlet.ChartServer?rsid=0FLE0000001MLKB&ruid=005E00000071TMb&did=01ZE0000000kI99&s=8&fs=12&tfg=12&tfs=-3407872&explode=0&c=gauge&cs=0&title=&eh=no&compo=no&fg=-16777216&bg1=-1&bg2=-1&bgdir=0&dl1=Fiscal+Period&dl2=&l=2&sax=yes&Yman=no&nc=0&actUrl=%2F00OE0000003668h%3Fdbw%3D1&sd=1&scv=no&sct=yes&spt=yes&bd=yes&cu=USD&ab=X&u=0&vt=0&ab2=Y&u2=4&vt2=3&vl0=Total+Bookings&spoc=no&topn=no&gm=0.0&gc0=-4041644&gm0=7000000.0&gc1=-4027564&gm1=9000000.0&gc2=-11222444&gm2=1.21E7&sona=0&refreshts=1446054614000"
    img3="https://na9.salesforce.com/servlet/servlet.ChartServer?rsid=0FLE0000001MLKD&ruid=005E00000071TMb&did=01ZE0000000kI99&s=8&fs=12&tfg=12&tfs=-3407872&explode=0&c=bar&cs=0&title=&eh=no&compo=no&fg=-16777216&bg1=-1&bg2=-1&bgdir=0&dl1=Fiscal+Period&dl2=Account+Name&l=2&sax=no&Yman=no&nc=0&actUrl=%2F00OE000000366CK%3Fdbw%3D1&sd=1&scv=yes&sct=no&spt=no&bd=yes&cu=USD&ab=X&u=0&vt=0&ab2=Y&u2=1&vt2=3&vl0=Best+Case&vl1=Expected+Case&vl2=Worst+Case&spoc=no&topn=no&gm=0.0&gc0=-1&gm0=1.0&gc1=-1&gm1=2.0&gc2=-1&gm2=3.0&sona=0&refreshts=1446054612000"
    img4="https://na9.salesforce.com/servlet/servlet.ChartServer?rsid=0FLE0000001R3dt&ruid=005E00000071TMb&did=01ZE0000000kI99&s=8&fs=12&tfg=12&tfs=-3407872&explode=0&c=column&cs=0&title=&eh=no&compo=yes&fg=-16777216&bg1=-1&bg2=-1&bgdir=0&dl1=Region&dl2=&l=2&sax=yes&Yman=no&nc=0&actUrl=%2F00OE00000036L8m%3Fdbw%3D1&sd=1&scv=yes&sct=no&spt=no&bd=yes&cu=USD&ab=X&u=0&vt=0&ab2=Y&u2=0&vt2=3&vl0=Sum+of+Worse+Case+Amount+%28converted%29&vl1=Sum+of+Expected+Case+Amount+%28converted%29&vl2=Sum+of+Best+Case+Amount+%28converted%29&spoc=no&topn=no&gc0=-1&gc1=-1&gc2=-1&sona=0&refreshts=1446054608000"
    img5="https://na9.salesforce.com/servlet/servlet.ChartServer?rsid=0FLE0000001R3ds&ruid=005E00000071TMb&did=01ZE0000000kI99&s=8&fs=12&tfg=12&tfs=-3407872&explode=0&c=line&cs=1&title=&eh=no&compo=yes&fg=-16777216&bg1=-1&bg2=-1&bgdir=0&dl1=Snapshot+Date&dl2=Case+Type&l=2&sax=yes&Yman=no&nc=0&actUrl=%2F00OE00000031Dpw%3Fdbw%3D1&sd=1&scv=no&sct=no&spt=no&bd=yes&cu=USD&ab=X&u=0&vt=0&ab2=Y&u2=0&vt2=3&vl0=Sum+of+Amount+%28converted%29&spoc=no&topn=no&gc0=-1&gc1=-1&gc2=-1&sona=0&refreshts=1446054608000"
    img6="https://na9.salesforce.com/servlet/servlet.ChartServer?rsid=0FLE0000001R3gY&ruid=005E00000071TMb&did=01ZE0000000kI99&s=8&fs=12&tfg=12&tfs=-3407872&explode=0&c=col_line&cs=0&title=&eh=no&compo=no&fg=-16777216&bg1=-1&bg2=-1&bgdir=0&dl1=Snapshot+Date&dl2=Fiscal+Period&l=2&sax=yes&Yman=no&nc=0&actUrl=%2F00OE00000036GGM%3Fdbw%3D1&sd=1&scv=no&sct=no&spt=no&bd=yes&cu=USD&ab=X&u=0&vt=0&ab2=Y&u2=0&vt2=3&vl0=ClosedWon&vl1=Expected&spoc=no&topn=no&gm=0.0&gc0=-1&gm0=1.0&gc1=-1&gm1=2.0&gc2=-1&gm2=3.0&sona=0&refreshts=1446054611000"
    img7="https://na9.salesforce.com/servlet/servlet.ChartServer?rsid=0FLE0000001R3gX&ruid=005E00000071TMb&did=01ZE0000000kI99&s=8&fs=12&tfg=12&tfs=-3407872&explode=0&c=bar&cs=0&title=&eh=no&compo=yes&fg=-16777216&bg1=-1&bg2=-1&bgdir=0&dl1=Classification&dl2=&l=2&sax=yes&Yman=no&nc=0&actUrl=%2F00OE00000036D9D%3Fdbw%3D1&sd=4&scv=yes&sct=no&spt=no&bd=yes&cu=USD&ab=X&u=0&vt=0&ab2=Y&u2=0&vt2=1&vl0=%25+Revenue&spoc=no&topn=no&gc0=-1&gc1=-1&gc2=-1&sona=0&refreshts=1446054606000"
    img8="https://na9.salesforce.com/servlet/servlet.ChartServer?rsid=0FLE0000001R3gU&ruid=005E00000071TMb&did=01ZE0000000kI99&s=8&fs=12&tfg=12&tfs=-3407872&explode=0&c=bar&cs=0&title=&eh=no&compo=yes&fg=-16777216&bg1=-1&bg2=-1&bgdir=0&dl1=Stage&dl2=&l=2&sax=yes&Yman=no&nc=0&actUrl=%2F00OE0000002wNl2%3Fdbw%3D1&sd=4&scv=yes&sct=no&spt=no&bd=yes&cu=USD&ab=X&u=0&vt=0&ab2=Y&u2=0&vt2=3&vl0=Sum+of+Amount+%28converted%29&spoc=no&topn=no&gc0=-1&gc1=-1&gc2=-1&sona=0&refreshts=1446054606000"
    img9="https://na9.salesforce.com/servlet/servlet.ChartServer?rsid=0FLE0000001R3gN&ruid=005E00000071TMb&did=01ZE0000000kI99&s=8&fs=12&tfg=12&tfs=-3407872&explode=0&c=pie&cs=0&title=&eh=no&compo=yes&fg=-16777216&bg1=-1&bg2=-1&bgdir=0&dl1=Role&dl2=&l=2&sax=yes&Yman=no&nc=0&actUrl=%2F00OE00000031D4G%3Fdbw%3D1&sd=1&scv=no&sct=no&spt=yes&bd=yes&cu=USD&ab=X&u=0&vt=0&ab2=Y&u2=0&vt2=3&vl0=Sum+of+Amount+%28converted%29&spoc=no&topn=no&gc0=-1&gc1=-1&gc2=-1&sona=0&refreshts=1446054608000"
    img10="https://na9.salesforce.com/servlet/servlet.ChartServer?rsid=0FLE0000001R3gW&ruid=005E00000071TMb&did=01ZE0000000kI99&s=8&fs=12&tfg=12&tfs=-3407872&explode=0&c=column&cs=0&title=&eh=no&compo=yes&fg=-16777216&bg1=-1&bg2=-1&bgdir=0&dl1=Opportunity+Record+Type&dl2=&l=2&sax=yes&Yman=no&nc=0&actUrl=%2F00OE00000036CzD%3Fdbw%3D1&sd=1&scv=yes&sct=no&spt=no&bd=yes&cu=USD&ab=X&u=0&vt=0&ab2=Y&u2=0&vt2=1&vl0=%25+of+Total&spoc=no&topn=no&gc0=-1&gc1=-1&gc2=-1&sona=0&refreshts=1446054608000"

    def __init__(self, browser):
        self.br=browser
        self.br.set_handle_robots(False)
        self.br.set_handle_equiv(True)
        self.br.set_handle_gzip(True)
        self.br.set_handle_redirect(True)
        self.br.set_handle_referer(True)

    def download_data(self,day):
        self.image_handling()
        self.table_handling()
        if day.isoweekday() in range(1, 6) and day.hour in range(10, 16):
            self.get_mails()

    def image_handling(self):

        image_array = []
        image_array.extend((self.img1, self.img2, self.img3, self.img4, self.img5, self.img6, self.img7, self.img8, self.img9, self.img10))

        self.add_headers()

        if self.is_signin():
            LOGGER.info("Cashed Data Handling")
            page_no = 0
            while page_no < len(image_array):
                r = self.open_browser(image_array[page_no])
                if r is False:
                    LOGGER.info("url open error url: "+image_array[page_no])
                    self.flag += 1
                    if self.flag > 5:
                        self.flag = 0
                        self.image_handling()
                        break
                else:
                    png = r.read()
                    LOGGER.info("downloading images")

                    f = open('../resources/www/page'+str(page_no)+'/a.png', 'wb')
                    f.write(png)
                    f.close()
                    page_no += 1

        else:
            if self.form_post():
                LOGGER.info("Log Into Site")
                self.image_handling()
            else:
                self.image_handling()

    def table_handling(self):
        self.add_headers()
        if self.is_signin():
            if self.save_table():
                LOGGER.info("Cashed Data Handling")
            else:
                self.table_handling()
        else:
            if self.form_post():
                LOGGER.info("Log Into Site")
                self.table_handling()
            else:
                self.table_handling()

    def add_headers(self):

        self.br.addheaders = [("User-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36"),
                              ("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"),
                              ("Accept-Language","en-US,en;q=0.8"),
                              ("Cache-Control","no-cache"),
                              ("Connection","keep-alive"),
                              ("Content-Type","application/x-www-form-urlencoded"),
                              ("Host","login.salesforce.com"),
                              ("Origin","//login.salesforce.com"),
                              ("Pragma","no-cache"),
                              ("Referer","https://login.salesforce.com/"),
                              ("Upgrade-Insecure-Requests","1")]

    def is_signin(self):

        response = self.open_browser(self.dashboard_url)
        if response is False:
            return False
        if 'Profile Name' in response.read():
            return True
        elif 'Verification' in response.read():
            LOGGER.info("Can't proceed")
            exit(1)
            # return False

    def form_post(self):
        response = self.open_browser('https://login.salesforce.com')

        if response is False :
            return False

        for f in self.br.forms():
            f.set_all_readonly(False)

        self.br.select_form("login")
        self.br["un"]="username"
        self.br["username"] = "username"
        self.br["pw"] = "password"
        response2=self.br.submit()

        if self.is_signin():
            return True
        else:
            return False

    def save_table(self):

        self.json_tables = {}
        j = 0

        page = self.open_browser(self.dashboard_url)
        if page is False:
            return False
        html = page.read()
        soup = BeautifulSoup(html, "lxml")

        tables = soup.find_all("table", class_="list")

        for table in tables:

            json_table = []
            i = 0

            for row in table.find_all('tr'):
                json_row = {}
                col = row.find_all('td')

                if len(col) == 2:
                    json_row['td'+str(i)] = col[1].find(text=True)
                    json_row['td'+str(i+1)] = col[0].findAll(text=True)

                if len(col) == 3:
                    json_row['td'+str(i)] = col[2].string
                    json_row['td'+str(i+1)] = col[1].string
                    json_row['td'+str(i+2)] = col[0].string

                if bool(json_row):
                    json_table.append(json_row)

            self.json_tables['table'+str(j)] = json_table
            j += 1

        self.write_to_json()
        return True

    def write_to_json(self):
        with open("../resources/www/table_json.json", 'w') as outfile:
            outfile.seek(0)
            json.dump(self.json_tables, outfile)

    def open_browser(self, url):
        try:
            response = self.br.open(url)
            LOGGER.info("Requested Url Opened: "+url)
            return response
        except Exception as inst:
            LOGGER.info(inst)
            return False

    def get_mails(self):
        user = "gmail@gmail.com"
        pwd = "pwd"
        notification_array=[]

        # connecting to the gmail imap server
        m = imaplib.IMAP4_SSL("imap.gmail.com")
        m.login(user,pwd)
        # print m.list()
        # m.select("Sales Force") # here you a can choose a mail box like INBOX instead
        m.select("Sales Force")
        # use m.list() to get all the mailboxes

        resp, items = m.search(None,"UNSEEN") # you could filter using the IMAP rules here (check http://www.example-code.com/csharp/imap-search-critera.asp)
        items = items[0].split() # getting the mails id

        for emailid in items:
            resp, data = m.fetch(emailid, "(RFC822)") # fetching the mail, "`(RFC822)`" means "get the whole stuff", but you can ask for headers only, etc
            email_body = data[0][1]  # getting the mail content

            raw_email_string = email_body.decode('utf-8')
            # converts byte literal to string removing b''
            email_message = email.message_from_string(raw_email_string)
            # this will loop through all the available multiparts in mail
            for part in email_message.walk():
                if part.get_content_type() == "text/plain": # ignore attachments/html
                    body = part.get_payload(decode=True)
                    body=body.translate(string.maketrans("\r\n", "  "))
                    content=body.split("   ")
                    notification_array.append({"from":email_message["From"],"subject":email_message["Subject"],"notification":content[1:]})
                    LOGGER.info("Mail reading")
                else:
                    continue

        m.logout()
        self.mail_to_json(notification_array)

    def mail_to_json(self,array):
        if len(array) > 0:
            with open("../resources/www/page14/mailing.json", 'w') as outfile:
                outfile.seek(0)
                json.dump(array, outfile)








