#!/usr/bin/python
# coding: UTF-8
import datetime
import argparse

def Parser ( src_name ):
    src_file = open(src_name, 'r')
    cnt = 0
    tokens = []
    for line in src_file:
        line = line.strip()
        line = line.replace('<-', ' ').replace('[', ' ').replace(']', ' ').replace('.', ' ').replace('(', ' ').replace(')', ' ').replace('\t', ' ').replace('\n', ' ')
        tokens.append(line.split())
    
    return tokens

def FillTab( NumTabs ):
    for _ in range(NumTabs):
        f_out.writelines('\t')

def Poke ( f_out, tokens ):
    Port_Name   = tokens[3]
    index       = 4
    if ',' in tokens:
        index = tokens.index(',')
    for no in range( 4, index + 1 ):
        Port_Name   = Port_Name + "_" + tokens[no].replace(',', ' ')
    Set_Value   = tokens[index + 1]
    for no in range( index + 2, len(tokens) ):
        Set_Value   = Set_Value + ' ' + tokens[no].replace(',', ' ')

    f_out.writelines('assign ' + Port_Name + ' = ' + Set_Value + '\n')

def Expect ( f_out, tokens ):
    Port_Name   = tokens[3]
    index       = 4
    if ',' in tokens:
        index = tokens.index(',')
    for no in range( 4, index + 1 ):
        Port_Name   = Port_Name + "_" + tokens[no].replace(',', '')
    Expct_Value = tokens[index + 1]
    for no in range( index + 2, len(tokens) ):
        Expct_Value = Expct_Value + ' ' + tokens[no].replace(',', '')

    f_out.writelines('NG = ' + Port_Name + ' != ' + Expct_Value + '\n')

def For_loop ( f_out, tokens ):
    index       = tokens[1]
    index_init  = tokens[2]
    bound_tkn   = tokens[3]
    bound_val   = tokens[4]
    operator    = ''

    if ( len( tokens ) > 4):
        if ( tokens[5] == 'by' ):
            stride  = tokens[6]
            if int(stride) >= 0:
                operator = ' + '
            else:
                operator = ' - '

    if ( index_init <= bound_val ):
        stride      = '1'
        operator    = ' + '
    elif ( index_init > bound_val ):
        stride      = '1'
        operator    = ' - '

    if ( bound_tkn == 'to' ):
        if ( int(stride) > 0 ):
            bound       = ' <= '
        else:
            bound       = ' >= '
    elif ( bound_tkn == 'until' ):
        if ( int(stride) > 0 ):
            bound       = ' < '
        else:
            bound       = ' > '       

    f_out.writelines('for( ' + index + ' = ' + str(index_init) + '; ' + index + bound + str(bound_val) + '; ' + index + ' = ' + index + operator + stride + ' ) {' +  '\n')

def If ( f_out, tokens ):
    f_out.writelines('if( ' + tokens[1] + ' ' + tokens[-3] + ' ' + tokens[-2] + ' ) {' + '\n')

def Random ( f_out, tokens ):
    index   = 1
    if '=' in tokens:
        index = tokens.index('=')
    f_out.writelines('assign ')
    f_out.writelines(tokens[0])
    for indx in range(1, index):
        f_out.writelines(' ' + tokens[indx])
    f_out.writelines(' = ' + '$random\n')

def Assn ( f_out, tokens ):
    Name    = tokens[0]
    index   = 1
    if '=' in tokens:
        index = tokens.index('=')
    for no in range( 1, index ):
        Name    = Name + "_" + tokens[no]
    Src     = tokens[index + 1]
    for no in range( index + 2, len(tokens) ):
        Src     = Src + " "  + tokens[no]

    f_out.writelines('assign ' + Name + ' = ' + Src + '\n')

def Var ( f_out, tokens ):
    Name    = tokens[1]
    Width   = tokens[len(tokens) - 1]
    if (len(tokens) > 3):
        f_out.writelines('reg   ' + Name + '[' + str(Width) + '-1 :0] ' + '\n')
    else:
        f_out.writelines('reg   ' + Name + '\n')

def Class ( f_out, tokens ):
    f_out.writelines(tokens[3] + '  ' + tokens[3] +'();\n\n')

def WriteLine( f_out, num_tab, all_tokens ):
    call_rc = False
    cnt = 0
    for tokens in all_tokens:
        print(tokens)
        if ( 'class' in tokens ):
            FillTab( num_tab )
            Class( f_out, tokens )
            cnt += 1
        elif ( 'expect' in tokens ):
            FillTab( num_tab )
            Expect( f_out, tokens )
            cnt += 1
        elif ( 'poke' in tokens ):
            FillTab( num_tab )
            Poke( f_out, tokens )
            cnt += 1
        elif ( 'var' in tokens ):
            FillTab( num_tab )
            Var( f_out, tokens )
            cnt += 1
        elif ( 'Random' in tokens ):
            FillTab( num_tab )
            Random( f_out, tokens )
            cnt += 1
        elif ( 'for' in tokens ):
            FillTab( num_tab )
            For_loop( f_out, tokens )
            cnt += 1
            call_rc = True
        elif ( 'if' in tokens ):
            FillTab( num_tab )
            If( f_out, tokens )
            cnt += 1
            call_rc = True
        elif ( 'import' in tokens ):
            _ = 0
            cnt += 1
        elif ( '=' in tokens):
            FillTab( num_tab )
            Assn( f_out, tokens )
            cnt += 1
        elif ( '}' in tokens):
            num_tab -= 1
            FillTab( num_tab )
            f_out.writelines('}\n\n')
            cnt += 1
        else:
            cnt += 1

        if (call_rc):
            num_tab += 1
            del all_tokens[0:cnt]
            cnt = 0
            WriteLine( f_out, num_tab, all_tokens )
        

#Main
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Neural Network Estimater')
    parser.add_argument('--src_file_name',   default='./TestBench.scala',  help='testbench src file name')
    parser.add_argument('--out_file_name',   default='./TestBench.v',      help='testbench out file name')

    args        = parser.parse_args()

    all_tokens  = Parser( args.src_file_name )

    f_out = open(args.out_file_name, 'w')
    f_out.writelines('module Test;\n') 
    num_tab = 1

    WriteLine( f_out, num_tab, all_tokens )
