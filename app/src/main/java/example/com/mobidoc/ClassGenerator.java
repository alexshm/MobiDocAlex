package example.com.mobidoc;


import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.google.dexmaker.Code;
import com.google.dexmaker.DexMaker;
import com.google.dexmaker.FieldId;
import com.google.dexmaker.Local;
import com.google.dexmaker.MethodId;
import com.google.dexmaker.TypeId;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Type;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import projections.Actions.Action;
import projections.Actions.MeasurementAction;

public class ClassGenerator {

    public ClassGenerator() {

    }
    public  void generateDex(){
        DexMaker dexMaker = new DexMaker();

       // Generate a HelloWorld class.
        TypeId<?> helloWorld = TypeId.get("LHelloTest;");

       //generate constractor
        //========================
        Code code = dexMaker.declare(helloWorld.getConstructor(), Modifier.PUBLIC);

        Local<?> thisRef = code.getThis(helloWorld);

        code.invokeDirect(TypeId.OBJECT.getConstructor(), null, thisRef);

        code.returnVoid();
        //========================

        dexMaker.declare(helloWorld, "HelloTest.java", Modifier.PUBLIC, TypeId.OBJECT);

        generateClass(dexMaker, helloWorld);
        // Create the dex file and load it.

        try {
            String path= Environment.getExternalStorageDirectory().getAbsoluteFile()+"/testfile.dex";
            File outputDir = new File(path);

            outputDir.setReadable(true);

            outputDir.setWritable(true);
            outputDir.createNewFile();

            byte[] data=dexMaker.generate();

            FileOutputStream stream = new FileOutputStream(outputDir);
            try {
                stream.write(data);
            } finally {
                stream.close();
            }

            System.out.println("Constractor build successfully");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void javaAssistGenerator(String fileName, Context c, ClassPool cp)
    {



         try {

               //generate the dynamic class
            //================================
           // final CtNewClass generated = new CtNewClass(generatedName, cp, false, superclass);
           // generated.toClass();
            CtClass[] NO_ARGS = {};
             cp.get(Action.class.getCanonicalName());
             CtClass cls = cp.makeClass("TestClass1");
            CtConstructor cons = new CtConstructor(null, cls);
           // cons.setBody(";");
            cls.addConstructor(cons);
            //CtConstructor constr = CtNewConstructor.defaultConstructor(cls);
            //constr.setBody("{}");

            System.out.println("Constractor build successfully");

            CtMethod mthd = CtNewMethod.make("public int printTest() {  return 5; }", cls);


            cls.addMethod(mthd);

            System.out.println("method !!! build successfully");

            ///write the generate class to file
            //===================================


             // System.out.println("the source is :\n"+ccFile.getClass().newInstance().getMethod("getInteger").toString());
           Class qc=cp.getAndRename("DynamicallyGenerated", "Bar")
                    .toClass();
             //=======================================
            ///generate instance
           // Example example = (Example) (new ByteCodeGenerator()).createInstance(clasz);
             //=======================================
          //  example.sayHello();

           // File outputDir = new File(path);
          //  cls.writeFile(Environment.getExternalStorageDirectory().getAbsoluteFile().toString());

             //=======================================
                // convert from "xxx.class" to "xxx.dex"

             //File path = Environment.getExternalStorageDirectory().getAbsoluteFile();
             //final DexFile dexFile = new DexFile(new DexOptions());
            // dexFile.setReadable(true);

             //dexFile.setWritable(true);
             //dexFile.createNewFile();

            // final DexFile df = new DexFile(cls.);
//
            // ClassDefItem item=new ClassDefItem(cls.toBytecode())

          //   ClassDefItem clazz=df.getClassOrNull("TestClass1") ;
           //  df.add(clazz);

             //final String dexFilePath = dexFile.getAbsolutePath();
         //    df.addClass(new File(getFilesDir(), "hoge.class"));
           //  df.writeFile(dexFilePath);

            // final DexFile df = new DexFile(fileName);
                //final String dexFilePath = DexFile.getAbsolutePath();

                //df.addClass(new File(getFilesDir(), "hoge.class"));
                //df.writeFile(dexFilePath);

            System.out.println("finish  generator class");

             String path= Environment.getExternalStorageDirectory().getAbsoluteFile().toString();
    //return  cls.toClass();
           // ConstPool constpool = ccFile.getConstPool();
           cls.writeFile(path);


             //final DexFile df = new DexFile("HelloDex.dex");
            // dexFile = new File(path, "HelloDex.dex");
             //final String dexFilePath = dexFile.getAbsolutePath();
            // dexFile.setReadable(true);

            // dexFile.setWritable(true);
            //dexFile.createNewFile();


             //options 2
            // ClassFile cf=cls.getClassFile();

           // cf.write(new DataOutputStream(new FileOutputStream(path+"/HelloDex.dex")));
               //          WriteToDex(dexFile);
             System.out.println("Done");
            System.out.println("finish  write DEX file");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        //return null;
    }
    /*
    private byte[] processBytecode(String s, byte[] bytes,CfOptions cfopt,DexOptions dexopt,DexFile dex) {
        ClassDefItem classDefItem = CfTranslator.translate(s + ".class", bytes, cfopt, dexopt);
        dex.add(classDefItem);
        //classNames.add(s);
        return bytes;
    }
    private void WriteToDex(DexFile dexfile){


        String path= Environment.getExternalStorageDirectory().getAbsoluteFile().toString();
        File tmpDex = new File(path,"DexTestClass.dex");

    byte[] dalvikBytecode = new byte[0];
    try {
        dalvikBytecode = dexfile.toDex(new OutputStreamWriter(new ByteArrayOutputStream()), false);
        File outputDir;
        FileOutputStream stream = new FileOutputStream(tmpDex);
        stream.write(dalvikBytecode);
        stream.close();
        stream.flush();


    } catch (IOException e) {
        Log.e("GrooidShell", "Unable to convert to Dalvik", e);
    }
    }

*/
    private void generateClass(DexMaker dexMaker, TypeId<?> declaringType) {
        {
            // Lookup some types we'll need along the way.
            TypeId<System> systemType = TypeId.get(System.class);
            TypeId<MeasurementAction> measure = TypeId.get(MeasurementAction.class);
            TypeId<PrintStream> printStreamType = TypeId.get(PrintStream.class);
            TypeId<Context> context = TypeId.get(Context.class);
            // Identify the 'hello()' method on declaringType.

            MethodId hello = declaringType.getMethod(TypeId.VOID, "hello");

            // Declare that method on the dexMaker. Use the returned Code instance
            // as a builder that we can append instructions to.
            Code code = dexMaker.declare(hello,  Modifier.PUBLIC);
            Local<PrintStream> localSystemOut = code.newLocal(printStreamType);
            // Declare all the locals we'll need up front. The API requires this.
            Local<MeasurementAction> a = code.newLocal(measure);
            Local<String> s =code.newLocal(TypeId.STRING);

            // int a = 0xabcd;
            // code.loadConstant(a,



           MethodId<?, Void> measureConstructor= measure.getConstructor(TypeId.STRING,TypeId.STRING,context);
            //code.invokeDirect(measureConstructor, measure, "888", new Local<?>[]{"55550", this});
            // String s = Integer.toHexString(c);
            MethodId<Integer, String> toHexString
                    = TypeId.get(Integer.class).getMethod(TypeId.STRING, "toHexString", TypeId.INT);
          //code.invokeStatic(toHexString, s, 4);

            // System.out.println(s);
            FieldId<System, PrintStream> systemOutField = systemType.getField(printStreamType, "out");
            code.sget(systemOutField, localSystemOut);
            MethodId<PrintStream, Void> printlnMethod = printStreamType.getMethod(
                    TypeId.VOID, "println", TypeId.STRING);
            code.invokeVirtual(printlnMethod, null, localSystemOut, s);

            // return;
            code.returnVoid();



        }
    }
}


