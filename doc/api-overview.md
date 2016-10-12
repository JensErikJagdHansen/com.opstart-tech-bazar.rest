## General GET
    GET  /lines
    GET  /operations

## Session
    POST /session/login/<userdata>
    GET  /session/logout/{UserID}
    GET  /session/break/{UserID}
    GET  /session/rejoin/{UserID}
    
## Basket
    GET  /basket/scan_create/{BasketID}
    GET  /basket/scan/{BasketID}
    POST /basket/status/<BasketInfo>

## Job
    GET  /jobinfo/{JobNr}
    GET  /baskets/{JobNr}
    POST /job/overview/<OverView>
    
## Sequences
x    GET  /sequence/standard/{ItemID}  (Still OK, but not needed)
    
    GET  /sequence/defecttypes (Still OK) 
    
x    GET  /sequence/rework/{DefectTypeID} (will not work, not needed)
    
	 GET  /sequence/{SequenceID}        (retunér fuld sekvens)



	 GET  /sequence/all-rework/{temID} (retuner alle rework seq for item, excl de enkelte trin, netto liste) 	 (med in job info)
    POST /sequence/item/
    
    
## Other stuff
    GET  /translations
    GET  /errormsg
    
## Statistics
    GET  /statistics/autoupdate/start
    GET  /statistics/autoupdate/stop
    GET  /statistics/update
    GET  /statistics/plan_actual<InfoKey>
    GET  /statistics/wip/{LineID}
    GET  /statistics/productivity/{LineID}
    GET  /statistics/overview/{LineID}
    GET  /statistics/manning/{LineID}
    
## Manage 
    GET  /log/delete/{days}
    GET  /sequence/update_operation_multipla
    
## Loadplan   
    GET  /loadplan/{LineID}
    GET  /loadplan/swap/{JobNr1}/{JobNr2}
    
## Off line output
    GET  /output/realised_production/{LineID}/{Load_YearWeek}/{SequenceType}/{Shift}
    GET  /output/loaded_jobnr/{LineID}/{Load_YearWeek}
    GET  /output/sql/{strSQL}
    
## Test stuff
    GET  /basket_test/{BasketID
    GET  /chart_test/{BasketID}
    
    
 
