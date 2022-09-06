package utils

//Interface som skal fokusere paa hvilken item (badested) brukeren klikker paa:
//Blir implementert i Badested- og FavorittAdapter:
interface OnClickListener {
    fun onItemClicked(position: Int)
}