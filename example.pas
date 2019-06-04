program zadaca;
var y, i : integer;

begin
y:=1;
y:=y+y+y;
writeln(y);
i:=0;
if (5 < 4) then
begin
	i:=50;
	y:=y+y+y+5;
end;
else
begin
	i:=100;
	y:=y+y+y+3;
end;
writeln(i);
	writeln(y);
end.