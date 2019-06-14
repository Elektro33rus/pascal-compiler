# pascal-compiler
<pascal program> -> 
[<program stat>] 
<declarations> 
<begin-statement>
 <program stat> -> E
<type> ->
	integer
	real
<var> ->
	[varname]
<numb> ->
	[intlet]
	[reallet]

<num> ->
	<var>
	<numb>

<declarations> -> 
<var decl><declarations>
<type ______,,______> 
<function ______,,______> 
-> E

<function decl> -> function <name> (params) : <type>;
<declarations> 
<begin-statement>
 		<statement> -> <function call>

<var decl> -> 
var[<namelist>: <type>;]^+ 

<begin_statement> -> 
begin <stats> end

 <stats> ->
 <while stat> ->
 while <cond> <begin_statement>
<if> ->
	if <cond> then <begin_statement>
if <cond> then <begin_statement> else <begin_statement>
 <for> ->
	for <num> to <num> do <begin_statement>
<writeStat> ->
	writeln (<num>)
<function call> ->
	[functionname] (<num>,^+)
<assignment>
	<var> := <num>
<cond> ->
     <num>  >  <num>
     <num>  <  <num>
     <num>  >=  <num>
     <num>  <=  <num>
     <num>  <>  <num>
     <num>  and  <num>
     <num>  ||  <num>
