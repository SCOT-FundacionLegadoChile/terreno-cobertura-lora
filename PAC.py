# noinspection PyUnresolvedReferences
import numpy as np
from xlrd import open_workbook

## INGRESAR DIRECCION DEL DOCUMENTO
Universo=input("Ingrese la ubicación del archivo Universo")
Salida1=input("Ingrese la ubicación de destino archivo nómina cobro")
Mes=input("Ingrese la fecha de la forma: Mes Año (Septiembre 2017)")
Fecha1=input("Ingrese fecha subida del documento de la forma AñoMesDia")
Fecha2=input("Ingrese fecha de cobro del banco de la forma AñoMesDia")
Salida2="Nómina "+Mes

## LEER DATOS DESDE EL EXCEL
i=0 # Para seleccionar sólo la primera hoja (Sheet)
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
                    monto = (sheet.cell(row2, 5).value)
                    Nombre.append(name[0:10])
                    Monto.append(str(int(monto)))
                    if value!=1 and value!=Mes:
                        end=row2
                        break

            values.append(value)


#LEER DATA DESDE EL ARCHIVO UNIVERSO
lineas=".........."
file_object  = open("Cobro noviembre.txt", "r")
Space="              "
Final=[]
contador=0
for line in file_object:
    Output=line[0:10]
    Output2=line[23:32]
    aux=str(Monto[contador]+"00")
    aux2=str(Nombre[contador])
    Final.append(Output+Output2+Space+aux2.ljust(10)+aux.zfill(11)+Fecha1+Fecha2+lineas)
    contador+=1

#/home/jaime/terreno-cobertura-lora/Cobro noviembre.txt
#Crear archivo y guardar datos.

file=open(Salida1+Salida2,'w')
for i in range(0,len(Final)):
    file.write(Final[i]+"\n")
file.close()
