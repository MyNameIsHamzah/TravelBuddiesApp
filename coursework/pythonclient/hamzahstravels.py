from tkinter import *
from tkinter import messagebox
import requests
import json
import datetime

window = Tk()
window.title("Hamzah's Travels")
window.geometry('1200x915')

# test params 52.9108067,-1.1829861 2022/01/23

#generateid

def generateid():
    useridresponse = requests.get("http://localhost:8080/api/v1/orchestratorservice/generateid")
    userid = json.loads(useridresponse.text)
    useridbox.delete("1.0", "end-1c")
    useridbox.insert(END, userid)
    

def tripProposal():
    userid = useridbox.get("1.0", "end-1c")
    latlon = locationcoordinates.get("1.0", "end-1c")
    thedate = date.get("1.0", "end-1c")

    todaysdate = datetime.datetime.today()
    usersdate = datetime.datetime.strptime(thedate, '%Y/%m/%d')

    print(todaysdate)
    daysdifference = (usersdate-todaysdate).days + 1
    print(daysdifference)
    
    if(daysdifference>14):
          messagebox.showerror("Date Error", " The date is too far in the future!")

    else: 
        posttripproposal = requests.get("http://localhost:8080/api/v1/orchestratorservice/submitoffer?latlon="+str(latlon)+"&date="+str(thedate)+"&userid="+str(userid))
    

def tripIntent():
    theuserid = useridbox.get("1.0", "end-1c")
    theproposaluserid = proposaluserid.get("1.0", "end-1c")
    themessage = message.get("1.0", "end-1c")
    posttripintent = requests.get("http://localhost:8080/api/v1/orchestratorservice/intentmessage?proposaluserid="+str(theproposaluserid)+"&message="+str(themessage)+"&userid="+str(theuserid))

def queryMessage():
    getquerymessage = requests.get("http://localhost:8080/api/v1/orchestratorservice/querymessage")
    querymessages = json.loads(getquerymessage.text)
    messagesbox.insert(END, querymessages)
    messagesbox.insert(END, " ")

def checkIntentMessages():
    getintentmessages = requests.get("http://localhost:8080/api/v1/orchestratorservice/checkintentmessages")
    intentmessages = json.loads(getintentmessages.text)
    messagesbox.insert(END, intentmessages)
    messagesbox.insert(END, " ")

def searchBar():
    searchquery = searchquerybox.get("1.0", "end-1c")
    theproposals = messagesbox.get("1.0", "end-1c")
    listofproposals = theproposals.split("} {")
    #start = listofproposals[0][1:]
    

    #print(listofproposals)
    array =[]
    for index, item in enumerate(listofproposals):
          if searchquery in item:
              array.append(item)
              #print(index, item)

    searchbox.delete("1.0", "end-1c")
    searchbox.insert(END, array)
        
    
#userid section
lbl = Label(window, text="UserID: ")
lbl.grid(column=0, row=0)

useridbox = Text(height = 1, 
              width =5)
useridbox.grid(column=1, row=0)

btn = Button(window, text="Generate", command = generateid)
btn.grid(column=3, row=0)



#trips proposal
lb2 = Label(window, text="Submit Trip Proposal")
lb2.grid(column=1, row=5)

lb3 = Label(window, text="Location Co-ordinates:")
lb3.grid(column=0, row=6)
locationcoordinates = Text(height = 1, 
              width =25)
locationcoordinates.grid(column=1, row=6)

lb4 = Label(window, text="Travel Date:")
lb4.grid(column=0, row=7)
date = Text(height = 1, 
              width =25)
date.grid(column=1, row=7)

btn2 = Button(window, text="Post Trip Proposal", command=lambda:tripProposal())

btn2.grid(column=1, row=8)

emptylabel = Label(window, text="                          ")
emptylabel.grid(column=1, row=9) 


#trip intent

lb5 = Label(window, text="Submit Trip Intent")
lb5.grid(column=1, row=10)


lb6 = Label(window, text="Proposal User ID:")
lb6.grid(column=0, row=11)
proposaluserid = Text(height = 1, 
              width =25)
proposaluserid.grid(column=1, row=11)

lb7 = Label(window, text="Message:")
lb7.grid(column=0, row=12)
message = Text(height = 3, 
              width =25)
message.grid(column=1, row=12)


btn3 = Button(window, text="Post Trip Intent", command=lambda:tripIntent())
btn3.grid(column=1, row=13)


#breaker
#emptylabel = Label(window, text="                          ")
#emptylabel.grid(column=5, row=9) 
#emptylabel2 = Label(window, text="                                      ")
#emptylabel2.grid(column=5, row=10)
#emptylabel3 = Label(window, text="                                      ")
#emptylabel3.grid(column=5, row=11)


#messagesbox

messagesbox = Text(height = 20, 
              width =92)
messagesbox.grid(column=4, row=9)

btn4 = Button(window, text="Query Messages", command=lambda:queryMessage())
btn4.grid(column=4, row=10)

btn5 = Button(window, text="Check Intent Messages", command=lambda:checkIntentMessages())
btn5.grid(column=4, row=11)

#searchbar and box
lb7 = Label(window, text="Search:")
lb7.grid(column=3, row=13)

searchquerybox = Text(height = 1, 
              width =92)
searchquerybox.grid(column=4, row=13)

searchbox = Text(height = 20, 
              width =92)
searchbox.grid(column=4, row=14)


btn7 = Button(window, text="Search", command=lambda:searchBar())
btn7.grid(column=4, row=15)



window.mainloop()
