program example;
var y, n, x : integer;

begin

y:=0;

while (y < 10) do
begin
	y:=y+1;
	if (y = 5) then
	begin
		continue;
	end;
	writeln(y);
end;

end.