{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
    buildInputs = with pkgs; [
      jdk17 gnumake
    ];
    env = {
      TEST="Hello there...";
      PROJ_HOME="~/code/dev/lblcs/dennys";
    };
    shellHook = ''
      alias home="cd $PROJ_HOME"
      cowsay $TEST
    '';
}
