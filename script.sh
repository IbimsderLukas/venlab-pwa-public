#!/bin/bash
# Speichere als "flatten-submodules.sh" und führe aus mit: bash flatten-submodules.sh

# Funktion um ein Submodul zu entfernen aber Dateien zu behalten
flatten_submodule() {
    local path=$1
    echo "Verarbeite: $path"
    
    # Inhalt sichern
    cp -r "$path" "${path}_backup"
    
    # Submodul deregistrieren
    git submodule deinit -f "$path"
    git rm -f "$path"
    rm -rf ".git/modules/$path"
    
    # Inhalt zurück (ohne .git Ordner)
    mv "${path}_backup" "$path"
    rm -rf "$path/.git"
    
    git add "$path"
}

# Alle Submodule finden und auflösen (von tief nach flach)
git submodule foreach --recursive 'echo $toplevel/$path' | tac | while read submodule; do
    if [ -d "$submodule" ]; then
        # Relativen Pfad berechnen
        rel_path=$(realpath --relative-to="$(pwd)" "$submodule")
        flatten_submodule "$rel_path"
    fi
done

# .gitmodules aus allen Ebenen entfernen
find . -name ".gitmodules" -exec rm {} \;
git add -A

git commit -m "Alle Submodule in reguläre Verzeichnisse konvertiert"