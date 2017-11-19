# noinspection PyUnresolvedReferences
import numpy as np
from xlrd import open_workbook

Mes="Septiembre 2017"
#Read cell from sheet xlsx.

i=0
wb = open_workbook('Registro Donaciones.xls')
for sheet in wb.sheets():
    i+=1
    if i==1:
        number_of_rows = sheet.nrows
        number_of_columns = sheet.ncols
        #print(number_of_rows)
        #print(number_of_columns)

        items = []

        rows = []
        values = []
        Nombre=[]
        Monto=[]

        for row in range (1,number_of_rows):
            value = (sheet.cell(row, 0).value)
            if value==Mes:
                start=row
                for row2 in range (start,number_of_rows):
                    value = (sheet.cell(row2, 0).value)
                    name  = (sheet.cell(row2, 2).value)
                    #monto_aux=[00000000000]
                    monto = (sheet.cell(row2, 5).value)
                    #monto_aux[]
                    Nombre.append(name[0:10])
                    Monto.append(str(int(monto)))
                    if value!=1 and value!=Mes:
                        end=row2
                        break

            values.append(value)
        #print(sheet.cell(0,0).value)
        #print(values)
        #print(start)
        #print(end)
        #print(Nombre)
        print(Monto)
        '''
        for row in range(1, number_of_rows):
            values = []
            for col in range(number_of_columns):
                value  = (sheet.cell(row,0).value)
                try:
                    value = str(int(value))
                except ValueError:
                    pass
                finally:
                    values.append(value)
            print(values)
            '''

#Read data from .txt

file_object  = open("Cobro noviembre.txt", "r")
#Output=[]
#Output2=[]
Space="              "
Final=[]
contador=0
for line in file_object:
    Output=line[0:10]
    Output2=line[23:32]
    aux=str(Monto[contador]+"00")
    Final.append(Output+Output2+Space+Nombre[contador]+aux.zfill(13))
    contador+=1
print(Final)

a="0020"
F=str(Monto[1]+a)
#a[-3:-1]=b
print(F.zfill(13))
