program example;
var x, n: integer;
   function fact (a: integer): integer;
   var y: integer;
   begin
        if (a <= 1) then
		begin
			a:=1;
		end;
        else
		begin
			y:=a-1;
			a:= a*fact(y);
		end;
   factresult:=a;
end;
begin
n:=10;
x:=fact(n);
writeln(x);
end.