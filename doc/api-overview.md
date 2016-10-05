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
    GET  /sequence/standard/{ItemID}
    GET  /sequence/defecttypes
    GET  /sequence/rework/{DefectTypeID}
    
    GET  /sequence/defecttypes/item/{ItemID}
    GET  /sequence/rework/{ReworkSequenceID}
    POST /sequence/rework/item/
    
    
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
    
    
 
