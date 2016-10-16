update [330_standard_sequences] set SequenceID='std-' + ItemID
update [330_standard_sequences] set SequenceType= 1
update [610_baskets] set SequenceID='std-' + ItemID
update [620_basket_log] set SequenceID='std-' + ItemID
update [620_basket_log_hist] set SequenceID='std-' + ItemID