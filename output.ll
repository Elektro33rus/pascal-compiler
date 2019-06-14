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
%3 = load i32, i32* %2
store i32 %3, i32* %"factresult"
%4 = load i32, i32* %"factresult"
ret i32 %4
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