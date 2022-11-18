DROP FUNCTION IF EXISTS getOffsetValue;
DELIMITER $$
CREATE OR REPLACE FUNCTION getOffsetValue(dayId INT, _hour INT, _minute INT, min_offset INT) RETURNS INT(11)
BEGIN
    DECLARE actHour, actMinute INT;
    IF _minute>=min_offset then
        SET actHour=_hour;
        SET actMinute=_minute-min_offset;
    else
        SET actHour=_hour-1;
        SET actMinute=(_minute + 60)- min_offset;
    end if;

    if actHour<7 or (actHour=7 and actMinute<=30) then
        return 0;
    end if;
    return (select value from blockfabrik_datapoint where day_id=dayId and hour=actHour and minute=actMinute limit 1);
end;
DELIMITER ;