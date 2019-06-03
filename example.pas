program zadaca;
var y, i,x : integer;

begin

for i:=0 to 10 do
	begin
		for y:=0 to 10 do
		begin
			x:=i+y;
			writeln(x);
		end;
	end;
end.