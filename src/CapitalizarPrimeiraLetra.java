public class CapitalizarPrimeiraLetra {

    public static String converter(String palavra){
        if(palavra == null || palavra.isEmpty()){

            return palavra;
        }

        palavra = palavra.toLowerCase();

        return palavra.substring(0, 1).toUpperCase() + palavra.substring(1);
    }
}
