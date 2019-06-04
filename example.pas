program zadaca;
var o, n, x : integer;

function fact(n : integer): integer;
begin
factresult:=0;
if (n = 1) then
		begin
			factresult:=1;
		end;
        else
		begin
			x:=n-1;
			factresult:=fact(x)*n;
		end;
end;

begin
n:=10;
o:=fact(n);
writeln(o);
end.