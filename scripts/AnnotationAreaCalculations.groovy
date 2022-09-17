import qupath.lib.gui.measure.ObservableMeasurementTableData

//Setting up Exports
imageName = getProjectEntry().getImageName()

String PATH = PROJECT_BASE_DIR

String directoryName = buildFilePath(PROJECT_BASE_DIR,"/csv");

File directory = new File(directoryName);
    if (! directory.exists()){
        directory.mkdir();
        // If you require it to make the entire directory path including parents,
        // use directory.mkdirs(); here instead.
    }

var rawPath = buildFilePath(PROJECT_BASE_DIR, "/csv/"+imageName+"_RawDetectionMeasurements.csv")
var path = buildFilePath(PROJECT_BASE_DIR, "/csv/TumorCellFeatures.csv")
var separator = ";"

//Creating Files
File file = new File(path)
header = "Image name"+separator
var exists = file.exists()

//Creating Measurements
def ob = new ObservableMeasurementTableData();
def annotations = getAnnotationObjects()

 // This line creates all the measurements
ob.setImageData(getCurrentImageData(),  annotations);

double stromaTotalT = 0
double stromaT = 0
double zellreichesStromaT = 0
double lockeresBindegewebeT = 0
double stromaTotalP = 0
double stromaP = 0
double zellreichesStromaP = 0
double lockeresBindegewebeP = 0
double tumor = 0
double necrosis = 0
double verhornung = 0
double peritumorTotal = 0

annotations.each { 
    if(it.getDisplayedName().equals("Stroma") && it.getParent().getDisplayedName().equals("tumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        stromaT=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Zellreiches Stroma") && it.getParent().getDisplayedName().equals("tumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        zellreichesStromaT=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Lockeres Bindegewebe") && it.getParent().getDisplayedName().equals("tumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        lockeresBindegewebeT=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Stroma") && it.getParent().getDisplayedName().equals("peritumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        stromaP=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Zellreiches Stroma") && it.getParent().getDisplayedName().equals("peritumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        zellreichesStromaP=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Lockeres Bindegewebe") && it.getParent().getDisplayedName().equals("peritumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        lockeresBindegewebeP=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Tumor") && it.getParent().getDisplayedName().equals("tumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        tumor=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Necrosis") && it.getParent().getDisplayedName().equals("tumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        necrosis=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("Verhornung") && it.getParent().getDisplayedName().equals("tumor") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        verhornung=ob.getNumericValue(it, "Area µm^2")
    }
    else if(it.getDisplayedName().equals("peritumor") && it.getParent().getDisplayedName().equals("Image") && !ob.getNumericValue(it, "Area µm^2").isNaN()){
        peritumorTotal=ob.getNumericValue(it, "Area µm^2")
    }
}
//print(stromaT+" "+zellreichesStromaT+" "+lockeresBindegewebeT+" "+stromaP+" "+zellreichesStromaP+" "+lockeresBindegewebeP+" "+tumor+" "+necrosis+" "+verhornung)
stromaTotalT=stromaT+zellreichesStromaT+lockeresBindegewebeT
stromaTotalP=stromaP+zellreichesStromaP+lockeresBindegewebeP

//print("Tumor Area    "+tumor+" µm^2")
//print("Stroma Total Area in Tumor   "+stromaTotalT+" µm^2")
//print("Stroma Total Area in Peritumor   "+stromaTotalP+" µm^2")


//print("Tumor/(Tumor + Stroma Total in Tumor)    "+100*tumor/(tumor+stromaTotalT)+" %")
//print("Necrosis/(Tumor + Necrosis)    "+100*necrosis/(tumor+necrosis)+" %")
//print("Zellreiches Stroma/ Stroma Total in Tumor    "+100*zellreichesStromaT/stromaTotalT+" %")
//print("Stroma/ Stroma Total in Tumor    "+100*stromaT/stromaTotalT+" %")
//print("Verhornung/(Tumor + Verhornung)    "+100*verhornung/(tumor+verhornung)+" %")


//print("Zellreiches Stroma/ Peritumor Total    "+100*zellreichesStromaP/peritumorTotal+" %")
//print("Stroma Total/ Peritumor Total    "+100*stromaTotalP/peritumorTotal+" %")

//Writing Files
file.withWriterAppend { fw -> 
    if (!exists){
        header = header + "Tumor [mm^2]" + separator + "Stroma Total in Tumor [mm^2]" + separator + "Stroma Total in Peritumor [mm^2]" + separator + "T/(T+STiT)%" + separator + "N/(T+N)%" + separator + "ZS/STiT%" + separator + "S/STiT%" + separator + "V/(T+V)%" + separator + "ZS/PtT%" + separator + "S/PtT%"
        fw.writeLine(header)
    }
    line=getProjectEntry().getImageName()+ separator + tumor*(10^(-6)) + separator + stromaTotalT*(10^(-6)) + separator + stromaTotalP*(10^(-6)) + separator + 100*tumor/(tumor+stromaTotalT) + separator + 100*necrosis/(tumor+necrosis) + separator + 100*zellreichesStromaT/stromaTotalT + separator + 100*stromaT/stromaTotalT + separator + 100*verhornung/(tumor+verhornung) + separator + 100*zellreichesStromaP/peritumorTotal + separator + 100*stromaTotalP/peritumorTotal
    line = line + System.getProperty("line.separator")
    fw.append(line)
}

