/* CREATE OR REPLACE FUNCTION updateStatusCount() RETURNS trigger AS $updateStatus$
		DECLARE
			old_status_count integer;
		BEGIN
			SELECT num_updates into old_status_count
			FROM NumberOfStatusUpdates
			WHERE userid = NEW.userid;
		
			IF (TG_OP = 'INSERT') THEN
				IF EXISTS (SELECT user_name from NumberOfStatusUpdates
				    WHERE userid = NEW.userid) THEN
					UPDATE NumberOfStatusUpdates
					SET num_updates = num_updates + 1
					WHERE userid = NEW.userid;
				ELSE
					INSERT INTO NumberOfStatusUpdates
					(userid,status_time,text)
					values(NEW.userid,NEW.name,1);
				END IF;
		
			ELSEIF (TG_OP = 'DELETE' AND old_status_count = 1) THEN
				DELETE FROM NumberOfStatusUpdates
				WHERE userid = NEW.userid;
			ELSE 
				UPDATE NumberOfStatusUpdates
				SET num_updates = num_updates - 1
				WHERE userid = NEW.userid;
			END IF;
		RETURN NEW;
		END;
$updateStatus$ LANGUAGE plpgsql;

CREATE TRIGGER update_num_status AFTER 
INSERT OR DELETE ON Status 
FOR EACH ROW EXECUTE PROCEDURE updateStatusCount();
END; */

CREATE OR REPLACE FUNCTION updateFlightCount() RETURNS trigger AS $updateFlight$

		DECLARE
			old_flight_count integer;
			customer_name char(30);
			
		BEGIN				
			IF (TG_OP = 'INSERT') THEN
				IF EXISTS (SELECT customerid from NumberOfFlightsTaken
				    WHERE customerid = NEW.customerid) THEN
					UPDATE NumberOfFlightsTaken
					SET numflights = numflights + 1
					WHERE customerid = NEW.customerid;
				ELSE	
					SELECT name into customer_name
					FROM customers
					WHERE customerid = NEW.customerid;
				
					INSERT INTO NumberOfFlightsTaken
					(customerid, customername, numflights)
					values(NEW.customerid,customer_name,1);
				END IF;
		
			ELSEIF (TG_OP = 'DELETE') THEN
			
				SELECT numflights into old_flight_count
				FROM NumberOfFlightsTaken
				WHERE customerid = OLD.customerid;
				
				IF (old_flight_count = 1) THEN 
					DELETE FROM NumberOfFlightsTaken
					WHERE customerid = OLD.customerid;
				ELSE 
					UPDATE NumberOfFlightsTaken
					SET numflights = numflights - 1
					WHERE customerid = OLD.customerid;
				END IF;
			END IF;
			
		RETURN NEW;
		END;
		
$updateFlight$ LANGUAGE plpgsql;

CREATE TRIGGER update_num_flights AFTER 
INSERT OR DELETE ON Flewon 
FOR EACH ROW EXECUTE PROCEDURE updateFlightCount();
END; 

