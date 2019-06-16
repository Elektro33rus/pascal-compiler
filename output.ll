@.strln = private unnamed_addr constant [2 x i8] c"\0A\00"

@.strint = private unnamed_addr constant [4 x i8] c"%i\0A\00"

@.strfloat = private unnamed_addr constant [4 x i8] c"%f\0A\00"

declare i32 @printf(i8*, ...)

@"x" = internal global i32 undef
@"n" = internal global i32 undef
define i32 @Func0(i32){
%2 = alloca i32
store i32 %0, i32* %2
%"factresult" = alloca i32
%"temp1" = alloca i32
%"temp2" = alloca i32
%3 = alloca i32
store i32 1, i32* %3
%4 = load i32, i32* %2
%5 = load i32, i32* %3
%6 = icmp sle i32 %4, %5
br i1 %6, label %7, label %10

;Label %7 ifthen
%8 = alloca i32
store i32 1, i32* %8
%9 = load i32, i32* %8
store i32 %9, i32* %2
br label %26

;Label %10 ifelse
%11 = alloca i32
store i32 1, i32* %11
%12 = load i32, i32* %2
%13 = load i32, i32* %11
%14 = sub i32 %12, %13
%15 = alloca i32
store i32 %14, i32* %15
%16 = load i32, i32* %15
store i32 %16, i32* %"temp1"
%17 = load i32, i32* %"temp1"
%18 = call i32 @Func0(i32 %17)
%19 = alloca i32
store i32 %18, i32* %19
%20 = load i32, i32* %19
store i32 %20, i32* %"temp2"
%21 = load i32, i32* %2
%22 = load i32, i32* %"temp2"
%23 = mul i32 %21, %22
%24 = alloca i32
store i32 %23, i32* %24
%25 = load i32, i32* %24
store i32 %25, i32* %2
br label %26

;Label %26 ifend
%27 = load i32, i32* %2
store i32 %27, i32* %"factresult"
%28 = load i32, i32* %"factresult"
ret i32 %28
}


define i32 @main() {
%1 = alloca i32
store i32 10, i32* %1
%2 = load i32, i32* %1
store i32 %2, i32* @"n"
%3 = load i32, i32* @"n"
%4 = call i32 @Func0(i32 %3)
%5 = alloca i32
store i32 %4, i32* %5
%6 = load i32, i32* %5
store i32 %6, i32* @"x"
%7 = load i32, i32* @"x"
%8 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strint, i32 0, i32 0), i32 %7)
ret i32 0
}