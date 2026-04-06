-- triggers

delimiter //
create trigger booking_after_insert
after insert on Booking
for each row
begin

update Booking
set BookingDate = NEW.BookingDate
where BookingID = NEW.BookingID;

end;
//
delimiter ;

-- booking date cant be in past
delimiter //
create trigger booking_before_insert
before insert on booking
for each row
begin

if new.bookingdate < curdate() then
signal sqlstate '45000'
set message_text = 'booking date cannot be in the past';
end if;
end //
delimiter ;
