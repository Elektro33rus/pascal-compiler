program zadaca;
var y, n, x : integer;

begin

x:=6;
y:=0;

while (y < 10) do
begin
	if (y = 5) then
	begin
		continue;
	end;
	y:=y+1;
	writeln(y);
end;

end.