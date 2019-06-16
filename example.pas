program example;
var x, n: integer;
   function fact(a : integer): integer;
   var temp1, temp2: integer;
   begin
        if (a <= 1) then
		begin
			a:=1;
		end;
        else
		begin
			temp1:=a-1;
			temp2:=fact(temp1);
			a:=a*temp2;
		end;
    factresult:=a;
	end;
begin
n:=10;
x:=fact(n);
writeln(x);
end.